import pyshark, socket

capture = pyshark.LiveCapture("Wi-Fi")
socket.setdefaulttimeout(500)
dnscache = {}
fromips = {}
destips = {}
count = 0
for packet in capture.sniff_continuously():
    if hasattr(packet, 'tcp'):
        # Resolve ip
        if packet.ip.src not in dnscache:
            try:
                fromhost = socket.gethostbyaddr(packet.ip.src) # Get hostname of ip
                dnscache[packet.ip.src] = fromhost[0] # cache with ip as key and hostname as value
            except socket.herror:
                dnscache[packet.ip.src] = packet.ip.src
        if packet.ip.dst not in dnscache:
            try:
                desthost = socket.gethostbyaddr(packet.ip.dst)
                print(desthost)
                dnscache[packet.ip.dst] = desthost[0]
            except socket.herror:
                dnscache[packet.ip.dst] = packet.ip.dst

        fromhost = dnscache[packet.ip.src]
        desthost = dnscache[packet.ip.dst]
        if fromhost in fromips: fromips[fromhost] += 1
        else: fromips[fromhost] = 1
        if desthost in destips: destips[desthost] += 1
        else: destips[desthost] = 1

        count+=1

        if count > 100:
            count = 0
            print("Source", fromips)
            print("Dest", destips)



