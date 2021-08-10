# !/usr/bin/env python
import numpy as np
import torch.nn.functional as F
import torch.nn as nn
import torch
import random
import os
import errno
from sklearn.metrics import classification_report

# The striker agent


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
        self.hidden_layer = 500
        # self.hidden_layer = 200
        self.in_norm = nn.BatchNorm1d(8)
        self.fc1 = nn.Linear(8, self.hidden_layer)
        self.fc1_norm = nn.BatchNorm1d(self.hidden_layer)
        self.fc2 = nn.Linear(self.hidden_layer, self.hidden_layer)
        self.fc2_norm = nn.BatchNorm1d(self.hidden_layer)
        self.fc3 = nn.Linear(self.hidden_layer, 5)

    def forward(self, x):
        x = x.view(-1, 8)
        x = self.in_norm(x)
        x = F.relu(self.fc1(x))
        x = self.fc1_norm(x)
        x = F.relu(self.fc2(x))
        x = self.fc2_norm(x)
        return F.log_softmax(self.fc3(x), dim=1)

class Agent:
    """
    The extended Agent class with specific heuritics
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

        # The training set.
        self.env_stack = list()
        self.act_stack = list()

    def get_data(self):
        return torch.stack(self.env_stack)

    def get_target(self):
        return torch.stack(self.act_stack).squeeze(1)

    def train(self, epochs=10):
        self.model.train()
        print('training policy..')

        for epoch in range(epochs):
            data = self.get_data()

            target = self.get_target()

            self.optimizer.zero_grad()

            output = self.model(data)

            loss = self.criterion(output, target)

            loss.backward()

            self.optimizer.step()

        print('Trained for {} epochs\n'.format(epochs))

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
    agent_mlp = Agent()
    agent_mlp.load_dataset(
        [r'C:\Users\zalat\Downloads\LFO-Simulator/traces-fourraydistance/trace-m0-StraightLineAgent.txt', ])
    agent_mlp.train()
