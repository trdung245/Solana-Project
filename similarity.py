import sys
import numpy as np
import tensorflow as tf
from keras._tf_keras.keras.preprocessing.image import load_img, img_to_array
from keras._tf_keras.keras.applications.vgg16 import VGG16, preprocess_input
from keras._tf_keras.keras.models import Model

def load_image(img_path, target_size=(224, 224)):
    img = load_img(img_path, target_size=target_size)
    img_array = img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    return preprocess_input(img_array)

def compute_similarity(img1_path, img2_path):
    # Load the pre-trained VGG16 model without the top fully-connected layers
    base_model = VGG16(weights='imagenet', include_top=False)

    # Define a new model that extracts features from the last convolutional layer
    model = Model(inputs=base_model.input, outputs=base_model.output)

    # Load and preprocess images
    img1 = load_image(img1_path)
    img2 = load_image(img2_path)

    # Get features from the model
    features1 = model.predict(img1)
    features2 = model.predict(img2)

    # Flatten the features
    features1 = np.ravel(features1)
    features2 = np.ravel(features2)

    # Compute cosine similarity
    cosine_similarity = np.dot(features1, features2) / (np.linalg.norm(features1) * np.linalg.norm(features2))
    return cosine_similarity

if __name__ == '__main__':
    img1_path = sys.argv[1]
    img2_path = sys.argv[2]

    similarity = compute_similarity(img1_path, img2_path)
    print(similarity)