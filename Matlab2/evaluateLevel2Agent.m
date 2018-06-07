% example: [a,b] = evaluateLevel2Agent('StraightLineAgent',7,1,8)

function [randomacc,learnedacc] = evaluateLevel2Agent(name,nmaps,CSIZE,XSIZE)
traces = cell(nmaps-1,1);
for i = 1:nmaps
  traces{i} = ['trace-m' num2str(i-1) '-' name '.txt'];
end

correct1 = 0;
correct2 = 0;
total = 0;


for testtrace = 1:nmaps
	VARS = CSIZE + XSIZE + 1;
	data = [];
	testdata = [];
	for i = 1:nmaps
		if i ~= testtrace
                        tmp = load(['M:/Desktop/workspace/LFOsimulator/traces-fourraydistance/' traces{i}]);
%                        tmp = tmp(1:100,:);
                        tmp2 = ones(size(tmp,1),XSIZE+1);
                        tmp2(:,1:XSIZE+1) = tmp(:,CSIZE+1:CSIZE+XSIZE+1);
			data = [data ; tmp2];
		else
                        tmp = load(['M:/Desktop/workspace/LFOsimulator/traces-fourraydistance/' traces{i}]);
%                        tmp = tmp(1:100,:);
                        tmp2 = ones(size(tmp,1),XSIZE+1);
                        tmp2(:,1:XSIZE+1) = tmp(:,CSIZE+1:CSIZE+XSIZE+1);
			testdata = [testdata ; tmp2];
			disp(['testing with ' traces{i}]);
                        %disp(testdata(1:20,:));
		end
	end
	VARS = XSIZE + 1;       % get rid of the C
	ncases = size(data, 1);			% number of data points
	cases = cell(VARS,ncases);		% create an empty table to store the data to be given to the learning algorithm
	cases(:,:) = num2cell(data(:,:)');	% copy the data
	
	dag = zeros(VARS,VARS);			% we will have VARS variables
	% Y depends on the rest:
	for x = 1:XSIZE
	  dag(x,VARS) = 1;
	end
	bnet = mk_bnet(dag, max([data ; testdata]), 'discrete', 1:VARS);	% creates a 2 variable bayes net, the last parameter specifies which variables are observable
	for i = 1:VARS
		bnet.CPD{i} = tabular_CPD(bnet, i);
	end

	engine1 = jtree_inf_engine(bnet);
        disp('Learning... LVL 2 Agnet');
	bnet2 = learn_params(bnet, cases);				% Learn using maximum likelihood
%	[bnet2, LLtrace] = learn_params_em(engine1, cases, EMIterations);	% learn using EM 
%        disp('Renormalizing...');
%	bnet2 = renormalizeDBNdistributions(bnet2);
	
%        disp('Dumping...');
%        dumpDBN(bnet2,['C:\my-research\FullBNT-1.0.7\santi-experiments\learned-bnets\bnet-l2o2-' name '-' num2str(testtrace) '.txt'],[1,CSIZE,XSIZE,1]);
	
        disp('Testing...');
	% Now test the model:
	engine2 = jtree_inf_engine(bnet2);

	for i=1:size(testdata, 1);
		evidence = cell(1,VARS);
		for j=1:VARS-1
			evidence{j} = testdata(i,j);
		end
		[engine1t, loglik1] = enter_evidence(engine1, evidence);
		[engine2t, loglik2] = enter_evidence(engine2, evidence);
		marg1 = marginal_nodes(engine1t, VARS);
		marg2 = marginal_nodes(engine2t, VARS);
		prediction1 = argmax(marg1.T);
		prediction2 = argmax(marg2.T);
%		disp(['input was: ' num2str(testdata(i,1:VARS))])
%        disp(['correct is: ' num2str(testdata(i,VARS)) ' answers were: ' num2str(prediction1) ' and ' num2str(prediction2)])
		if prediction1 == testdata(i,VARS)
			correct1 = correct1 + 1;
		end
		if prediction2 == testdata(i,VARS)
			correct2 = correct2 + 1;
		end
		total = total + 1;
	end
        disp(['Complete: ' num2str(correct1) '/' num2str(total) ' = ' num2str(correct1/total)]);
        disp(['Complete: ' num2str(correct2) '/' num2str(total) ' = ' num2str(correct2/total)]);
end
randomacc = correct1/total;
learnedacc = correct2/total;