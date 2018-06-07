% example: a = learnNN(['traces-fourraydistance/trace-m0-SmartRandomAgent-nnet.txt';'traces-fourraydistance/trace-m1-SmartRandomAgent-nnet.txt'],8,5)
% example: a = learnNN(['traces-fourraydistance/trace-m0-SmartRandomExplorerAgent-nnet.txt';'traces-fourraydistance/trace-m1-SmartRandomExplorerAgent-nnet.txt'],8,5)

function [net] = learnNN(traces,XSIZE,YSIZE)

data = [];
for i = 1:size(traces,1)
  tmp = load(traces(i,:));
  data = [data ; tmp];
end

inputs = data(:,[1:XSIZE])';
outputs = data(:,[XSIZE+1:XSIZE+YSIZE])';
        
% Create and learn the NN:
net = feedforwardnet(10);
net.trainParam.showWindow = false;
net = train(net,inputs,outputs);
