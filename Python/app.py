from flask import Flask, jsonify, request
import numpy
import torch
from .agent_lstm import Agent as LSTMAgent
from .agent_mlp import Agent as MLPAgent

app = Flask(__name__)
lstm_agent = LSTMAgent()
mlp_agent = MLPAgent()
ITERATIONS = 1000

@app.route('/lstm/predict', methods=['POST'])
def lstm_predict():
    # Get the json data.
    content = request.get_json(force=True)
    print(content)
    data = numpy.array(content['data'])
    return jsonify({'action': lstm_agent.predict(data - 1)})

@app.route('/lstm/train', methods=['POST'])
def lstm_train():
    # Get the json data.
    content = request.get_json(force=True)
    print(content)
    lstm_agent.load_dataset(content['filenames'])
    lstm_agent.train(ITERATIONS)
    return jsonify({'training': 'Success'})

@app.route('/lstm/reset', methods=['POST'])
def lstm_reset():
    # Get the json data.
    lstm_agent = LSTMAgent()
    return jsonify({'reset': 'Success'})

@app.route('/mlp/predict', methods=['POST'])
def mlp_predict():
    # Get the json data.
    content = request.get_json(force=True)
    print(content)
    data = numpy.array(content['data'])
    return jsonify({'action': mlp_agent.predict(data - 1)})

@app.route('/mlp/train', methods=['POST'])
def mlp_train():
    # Get the json data.
    content = request.get_json(force=True)
    print(content)
    mlp_agent.load_dataset(content['filenames'])
    mlp_agent.train(ITERATIONS)
    return jsonify({'training': 'Success'})

@app.route('/mlp/reset', methods=['POST'])
def mlp_reset():
    # Get the json data.
    mlp_agent = MLPAgent()
    return jsonify({'reset': 'Success'})

if __name__ == '__main__':
    app.run()
