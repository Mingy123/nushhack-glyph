import pyautogui
import tensorflow as tf
import numpy as np
import time
import json
import os

TF_MODEL_FILE_PATH = '../assets/model.tflite' # The default path to the saved TensorFlow Lite model
CHANGE_VALUE = 10

interpreter = tf.lite.Interpreter(model_path=TF_MODEL_FILE_PATH)
classify_lite = interpreter.get_signature_runner('serving_default')

while True:
    pyautogui.screenshot().save("scrnsht.png")
    # Get and load image
    img = tf.keras.utils.load_img(
        "scrnsht.png", target_size=(360, 640)
    )
    img_array = tf.keras.utils.img_to_array(img)
    img_array = tf.expand_dims(img_array, 0) # Create a batch

    # Classify image
    predictions_lite = classify_lite(rescaling_6_input=img_array)['dense_8']
    score_lite = tf.nn.softmax(predictions_lite)

    # Output
    class_names = ['.ipynb_checkpoints', 'dumb', 'work']
    print(
        "This image most likely belongs to {} with a {:.2f} percent confidence."
            .format(class_names[np.argmax(score_lite)], 100 * np.max(score_lite))
    )

    # Write to file
    if not os.path.exists("../base.dat"): # create base.dat if it doesnt exist
        with open("../base.dat", "w") as file:
            file.write("0\n")
            file.close()

    if not os.path.exists("../.base.lock"): # check that base lock doesnt exist
        filelines = []
        with open("../base.dat", "r") as file:
            filelines = file.readlines()
            file.close()

        print(filelines)

        with open("../base.dat", "w") as file:
            coins = int(filelines[0])

            # Decision making
            if np.argmax(score_lite) == 2:
                coins += CHANGE_VALUE
            elif np.argmax(score_lite) == 1:
                coins -= CHANGE_VALUE
            try:
                file.write(str(coins) + "\n")
                for i in range(1, len(filelines)):
                    file.write(filelines[i])
                file.close()
            except KeyboardInterrupt:
                print("killing, aborting write")

    else:
        while os.path.exists("../.base.lock"):
            print("Waiting for unlock...")
            time.sleep(1)

    time.sleep(10)
