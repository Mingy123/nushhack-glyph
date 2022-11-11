import tensorflow as tf
import numpy as np

TF_MODEL_FILE_PATH = 'model.tflite' # The default path to the saved TensorFlow Lite model

interpreter = tf.lite.Interpreter(model_path=TF_MODEL_FILE_PATH)
classify_lite = interpreter.get_signature_runner('serving_default')

# Get and load image
img = tf.keras.utils.load_img(
    "hax.png", target_size=(360, 640)
)
img_array = tf.keras.utils.img_to_array(img)
img_array = tf.expand_dims(img_array, 0) # Create a batch

# Classify image
print(classify_lite(rescaling_6_input=img_array))
predictions_lite = classify_lite(rescaling_6_input=img_array)['dense_8']
score_lite = tf.nn.softmax(predictions_lite)

# output
class_names = ['.ipynb_checkpoints', 'dumb', 'work']
print(
    "This image most likely belongs to {} with a {:.2f} percent confidence."
        .format(class_names[np.argmax(score_lite)], 100 * np.max(score_lite))
)
print(score_lite)