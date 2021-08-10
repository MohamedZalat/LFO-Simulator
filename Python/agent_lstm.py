# !/usr/bin/env python
import numpy as np
import torch.nn.functional as F
import torch.nn as nn
import torch
import random
import os
import errno
from sklearn.metrics import classification_report

# The LSTM agent


# methods from actionHandler are
# CATCH = "catch"(rel_direction)
# CHANGE_VIEW = "change_view"
# DASH = "dash"(power)
# KICK = "kick"(power, rel_direction)
# MOVE = "move"(x,y) only pregame
# SAY = "say"(you_can_try_cursing)
# SENSE_BODY = "sense_body"
# TURN = "turn"(rel_degrees in 360)
# TURN_NECK = "turn_neck"(rel_direction)

# potentially useful from aima
# learning.py
# mdp
#
class Net(nn.Module):
    def __init__(self):
        super(Net, self).__init__()
        self.input_dim = 8
        self.hidden_dim = 128
        # self.hidden_dim = 20
        print(self.hidden_dim)
        self.n_layers = 1
        self.hidden = None
        self.device = None

        self.in_norm = nn.BatchNorm1d(self.input_dim)
        self.lstm_norm = nn.BatchNorm1d(self.hidden_dim)
        self.lstm = nn.LSTM(self.input_dim,
                            self.hidden_dim,
                            self.n_layers,
                            batch_first=True)
        self.out = nn.Linear(self.hidden_dim, 5)

    def forward(self, x, init_hidden=False):
        x = x.view(-1, self.input_dim)

        if self.hidden is None or init_hidden:
            self.hidden = self.init_hidden(1)

        x = self.in_norm(x)

        # Input is always a single batch of a variable sequence.
        x = x.unsqueeze(0)

        x, self.hidden = self.lstm(x, self.hidden)

        self.hidden = (self.hidden[0].detach(),
                       self.hidden[1].detach())

        x = x.contiguous().view(-1, self.hidden_dim)

        x = self.lstm_norm(F.relu(x))
        x = self.out(x)
        return F.log_softmax(x, dim=1)

    def init_hidden(self, batch_size):
        weight = next(self.parameters()).data
        self.hidden = (weight.new(self.n_layers, batch_size, self.hidden_dim).zero_().to(self.device),
                       weight.new(self.n_layers, batch_size, self.hidden_dim).zero_().to(self.device))

    def switch_to_train(self):
        # Requires the model to have been trained first.
        pass

class Agent:
    """
    The extended Agent class with specific heuristics
    """
    def __init__(self):
        self.model = Net()
        self.optimizer = torch.optim.Adam(self.model.parameters(), lr=1e-3)
        # self.optimizer = torch.optim.SGD(self.model.parameters(), lr=1e-4)
        self.criterion = nn.CrossEntropyLoss()

        self.device = torch.device('cuda') if torch.cuda.is_available() \
            else torch.device('cpu')

        # Print the device we are using.
        print('device = {}\n'.format(self.device))
        self.model.device = self.device
        self.model.to(self.device)

        self.model.init_hidden(1)

        # The training set.
        self.env_stack = list()
        self.act_stack = list()

    def get_data(self):
        return torch.stack(self.env_stack)

    def get_target(self):
        return torch.stack(self.act_stack).squeeze(1)

    def train(self, epochs=10):
        # Save the last hidden state to use when back in evaluation mode.
        hidden = self.model.hidden

        self.model.train()
        print('training policy..')

        for epoch in range(epochs):
            data = self.get_data().view(-1, 8)
            target = self.get_target()
            output = self.model(data, init_hidden=True)

            loss = self.criterion(output, target)

            # loss.backward(retain_graph=epoch != (epochs - 1))
            loss.backward()

            # Clip is 5.
            nn.utils.clip_grad_norm_(self.model.parameters(), 5)
            self.optimizer.step()

        print('Trained for {} epochs\n'.format(epochs))

        self.model.hidden = hidden

    def predict(self, env):
        self.model.eval()
        data = torch.from_numpy(env).float().to(self.device)

        output = self.model(data)
        selected_action = torch.argmax(output, dim=1).item()

        print('selected_action = {}'.format(selected_action))

        return selected_action

    def load_dataset(self, file_names):
        for file_name in file_names:
            # If the dataset directory does not exist, do nothing
            if not os.path.exists(file_name):
                print('File does not exist. {}'.format(file_name))
                return

            file_env_act = open(file_name, 'r')
            env_act_stack = np.loadtxt(file_env_act)

            env_stack = env_act_stack[:, :-1] - 1

            # We subtract one to make it follow the pytorch format.
            act_stack = env_act_stack[:, -1] - 1

            print(env_stack)
            print(act_stack)

            file_env_act.close()

            for env, act in zip(env_stack, act_stack):
                self.env_stack.append(torch.from_numpy(env).float().to(self.device))
                self.act_stack.append(torch.Tensor([int(act)]).long().to(self.device).view(-1))

        return self.env_stack, self.act_stack

if __name__ == "__main__":
    agent_lstm = Agent()
    agent_lstm.load_dataset(
        [r'C:\Users\zalat\Downloads\LFO-Simulator/traces-fourraydistance/trace-m0-StraightLineAgent.txt', ])
    agent_lstm.train()
