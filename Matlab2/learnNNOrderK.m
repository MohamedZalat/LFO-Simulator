% nnet1 = learnNNOrderK(['traces-fourraydistance/trace-m1-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m2-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m3-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m4-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m5-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m6-WallFollowerAgent-nnet.txt';],8,5,2,0);

function [net] = learnNNOrderK(traces,XSIZE,YSIZE,ORDER,YDEFAULT)

data = [];
for i = 1:size(traces,1)
  tmp = load(traces(i,:));
  for j = 1:ORDER-1
  	firstrow = tmp(1,:);
  	for k = 1:YSIZE
  	  firstrow(XSIZE+k) = YDEFAULT;
  	end
	tmp2 = [firstrow ; tmp(1:size(tmp,1)-1,:)];
	tmp = [tmp2 tmp];
  end
  data = [data ; tmp];
end

XSIZE = XSIZE*ORDER + YSIZE*(ORDER-1);

inputs = data(:,[1:XSIZE])';
outputs = data(:,[XSIZE+1:XSIZE+YSIZE])';

disp(inputs(:,1:10)');
disp(outputs(:,1:10)');
        
% Create and learn the NN:
net = feedforwardnet(10);
net.trainParam.showWindow = false;
net = train(net,inputs,outputs);
