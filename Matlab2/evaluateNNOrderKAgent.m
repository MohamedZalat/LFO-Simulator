% acc = evaluateNNOrderKAgent(['traces-fourraydistance/trace-m0-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m1-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m2-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m3-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m4-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m5-WallFollowerAgent-nnet.txt';'traces-fourraydistance/trace-m6-WallFollowerAgent-nnet.txt';],8,5,2,0);

function [learnedacc] = evaluateNNOrderKAgent(name,prefix,nmaps,CSIZE,XSIZE,ORDER)
YSIZE = 5;
YDEFAULT = 0;
traces = cell(nmaps-1,1);
for i = 1:nmaps
  traces{i} = ['trace-m' num2str(i-1) '-' name '-nnet.txt'];
end

correct2 = 0;
total = 0;

for testtrace = 1:nmaps
    data = [];
    testdata = [];
    for i = 1:size(traces,1)
    	if i ~= testtrace
          tmp = load([prefix traces{i}]);
%          tmp = tmp(1:100,:);
          for j = 1:ORDER-1
            firstrow = tmp(1,:);
            for k = 1:YSIZE
              firstrow(XSIZE+k) = YDEFAULT;
            end
            tmp2 = [firstrow ; tmp(1:size(tmp,1)-1,:)];
            tmp = [tmp2 tmp];
          end
          data = [data ; tmp];
    	else
          tmp = load([prefix traces{i}]);
%          tmp = tmp(1:100,:);
          for j = 1:ORDER-1
            firstrow = tmp(1,:);
            for k = 1:YSIZE
              firstrow(XSIZE+k) = YDEFAULT;
            end
            tmp2 = [firstrow ; tmp(1:size(tmp,1)-1,:)];
            tmp = [tmp2 tmp];
          end
          testdata = [testdata ; tmp];
        end
	end

    disp(['data: ' num2str(size(data))]);
    disp(['testdata: ' num2str(size(testdata))]);

    INPUTSIZE = XSIZE*ORDER + YSIZE*(ORDER-1);

    inputs = data(:,[1:INPUTSIZE])';
    outputs = data(:,[INPUTSIZE+1:INPUTSIZE+YSIZE])';

    testinputs = testdata(:,[1:INPUTSIZE])';
    testoutputs = testdata(:,[INPUTSIZE+1:INPUTSIZE+YSIZE])';

    net = feedforwardnet(10);
    net.trainParam.showWindow = false;
    net = train(net,inputs,outputs);
                	
	% Now test the model:
	for i=1:size(testdata, 1);
		evidence = testinputs(:,i);
%        disp(['input: ' num2str(evidence')])
        output = net(evidence);
		prediction = argmax(output);
        prediction2 = argmax(testoutputs(:,i));
		if prediction2 == prediction
			correct2 = correct2 + 1;
		end
%        disp([num2str(prediction) ' -> ' num2str(prediction2) '  (' num2str(output') ')']);
		total = total + 1;
	end
	disp(['current: ' num2str(correct2/total)]);
end
learnedacc = correct2/total;
