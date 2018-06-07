% example: a = evaluateNNAgent('StraightLineAgent',7,1,8)

function [learnedacc] = evaluateNNAgent(name,nmaps,CSIZE,XSIZE)
traces = cell(nmaps-1,1);
for i = 1:nmaps
  traces{i} = ['trace-m' num2str(i-1) '-' name '.txt'];
end

correct2 = 0;
total = 0;

for testtrace = 1:nmaps
	VARS = CSIZE + XSIZE + 1;					 
	data = [];
	testdata = [];
	for i = 1:nmaps
		if i ~= testtrace
                        tmp = load(['/Users/santi/NetBeansProjects/LFOSimulator/traces-fourraydistance/' traces{i}]);
%                        tmp = tmp(1:100,:);
                        for i1 = 1:size(tmp,1)
                            for i2 = 1:CSIZE+XSIZE
                                tmp(i1,i2) = tmp(i1,i2) - 1;
                            end
                        end
			data = [data ; tmp];
		else
                        tmp = load(['/Users/santi/NetBeansProjects/LFOSimulator/traces-fourraydistance/' traces{i}]);
%                        tmp = tmp(1:100,:);
                        for i1 = 1:size(tmp,1)
                            for i2 = 1:CSIZE+XSIZE
                                tmp(i1,i2) = tmp(i1,i2) - 1;
                            end
                        end
			testdata = [testdata ; tmp];
			disp(['testing with ' traces{i}]);
                        %disp(testdata(1:20,:));
		end
            end
        inputs = data(:,[CSIZE+1:CSIZE+XSIZE])';
        tmp_outputs = data(:,[CSIZE+XSIZE+1:CSIZE+XSIZE+1])';
        outputs = zeros(5,size(tmp_outputs,2));
        for i = 1:size(outputs,2)
            outputs(tmp_outputs(i),i) = 1;
        end
        
        test_inputs = testdata(:,[CSIZE+1:CSIZE+XSIZE])';

        % Create and learn the NN:
        net = feedforwardnet(10);
        net.trainParam.showWindow = false;
        net = train(net,inputs,outputs);
	
	% Now test the model:
	for i=1:size(testdata, 1);
		evidence = test_inputs(:,i);
        output = net(evidence);
		prediction2 = argmax(output);
%        disp(['input: ' num2str(evidence')])
%        disp(['prediction: ' num2str(output') '  real was: ' num2str(testdata(i,CSIZE+XSIZE+1))]);
		if prediction2 == testdata(i,CSIZE+XSIZE+1)
			correct2 = correct2 + 1;
		end
		total = total + 1;
	end
	correct2/total
end
learnedacc = correct2/total;
