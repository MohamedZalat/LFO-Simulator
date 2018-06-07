% example: learnNNWeights(['traces-fourraydistance/trace-m0-SmartRandomAgent-nnet.txt';'traces-fourraydistance/trace-m1-SmartRandomAgent-nnet.txt'],8,5)
% example: [IW,LW,B1,B2,ranges] = learnNNWeights(['traces-fourraydistance/trace-m0-SmartRandomExplorerAgent-nnet.txt';'traces-fourraydistance/trace-m1-SmartRandomExplorerAgent-nnet.txt'],8,5)

function [IW,LW,B1,B2,ranges] = learnNNWeights(traces,XSIZE,YSIZE)

data = [];
for i = 1:size(traces,1)
  tmp = load(traces(i,:));
  data = [data ; tmp];
end

ranges = zeros(XSIZE+YSIZE,2);
for i = 1:XSIZE+YSIZE
    ranges(i,1) = min(data(:,i));
    ranges(i,2) = max(data(:,i));
end

inputs = data(:,[1:XSIZE])';
outputs = data(:,[XSIZE+1:XSIZE+YSIZE])';

% Create and learn the NN:
net = feedforwardnet(10);
net.trainParam.showWindow = false;
net = train(net,inputs,outputs);
IW = net.IW{1};
LW = net.LW{2,1};
B1 = net.B{1};
B2 = net.B{2};