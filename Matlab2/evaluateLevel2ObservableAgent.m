% example: [a,b] = evaluateLevel2ObservableAgent('StraightLineAgent',7,1,8)

function [randomacc,learnedacc] = evaluateLevel2ObservableAgent(name,nmaps,CSIZE,XSIZE)
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
			data = [data ; load(['M:\\Desktop\\workspace\\LFOsimulator\\traces-fourraydistance2\\' traces{i}])];
		else
			testdata = [testdata ; load(['M:\\Desktop\\workspace\\LFOsimulator\\traces-fourraydistance2\\' traces{i}])];
			['testing with ' traces{i}] 
		end
	end
	ncases = size(data, 1);			% number of data points
	cases = cell(VARS,ncases);		% create an empty table to store the data to be given to the learning algorithm
	cases = num2cell(data');		% copy the data, since the first variable is hidden, we don't copy it
	
	dag = zeros(VARS,VARS);			% we will have VARS variables
	% C depends on X:
	for c = 1:CSIZE
	  for x = 1:XSIZE
		dag(x+CSIZE,c) = 1;
	  end
	end
	% Y depends on X and C:
	for x = 1:XSIZE
	  dag(x+CSIZE,1+CSIZE+XSIZE) = 1;
	end
	for c = 1:CSIZE
	  dag(c,1+CSIZE+XSIZE) = 1;
	end
	bnet = mk_bnet(dag, max([data ; testdata]), 'discrete', 1:VARS);	% creates a 2 variable bayes net, the last parameter specifies which variables are observable
	for i = 1:VARS
		bnet.CPD{i} = tabular_CPD(bnet, i);
	end

	bnet2 = learn_params(bnet, cases);				% Learn using maximum likelihood

	% Now test the model:
	engine1 = jtree_inf_engine(bnet);
	engine2 = jtree_inf_engine(bnet2);

	for i=1:size(testdata, 1);
		evidence = cell(1,VARS);
		for j=1:CSIZE+XSIZE
			evidence{j} = testdata(i,j);
		end
		[engine1t, loglik1] = enter_evidence(engine1, evidence);
		[engine2t, loglik2] = enter_evidence(engine2, evidence);
		marg1 = marginal_nodes(engine1t, CSIZE+XSIZE+1);
		marg2 = marginal_nodes(engine2t, CSIZE+XSIZE+1);
		prediction1 = argmax(marg1.T);
		prediction2 = argmax(marg2.T);
		if prediction1 == testdata(i,CSIZE+XSIZE+1)
			correct1 = correct1 + 1;
		end
		if prediction2 == testdata(i,CSIZE+XSIZE+1)
			correct2 = correct2 + 1;
		end
		total = total + 1;
	end
	correct1/total
	correct2/total
end
randomacc = correct1/total;
learnedacc = correct2/total;