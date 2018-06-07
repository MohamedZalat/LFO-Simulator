% example: [a,b] = evaluateLevel2ContinuousAgent('ForceStraightLineAgent',7,10,2)

function [randomacc,learnedacc] = evaluateLevel2ContinuousAgent(name,nmaps,XSIZE,YSIZE)
traces = cell(nmaps-1,1);
for i = 1:nmaps
  traces{i} = ['trace-m' num2str(i-1) '-' name '.txt'];
end

total_mse1 = zeros(YSIZE,1);
total_mse2 = zeros(YSIZE,1);
total = 0;


for testtrace = 1:nmaps
	VARS = XSIZE + YSIZE;
	data = [];
	testdata = [];
	for i = 1:nmaps
        tmp = load(['M:/Desktop/workspace/LFOsimulator/traces-forcefourraydistance/' traces{i}]);
		tmp = tmp(1:200,:);
		if i ~= testtrace
			data = [data ; tmp];
		else
			testdata = [testdata ; tmp];
			disp(['testing with ' traces{i}]);
                        %disp(testdata(1:20,:));
		end
	end
	ncases = size(data, 1);			% number of data points
	cases = cell(VARS,ncases);		% create an empty table to store the data to be given to the learning algorithm
	cases(:,:) = num2cell(data(:,:)');	% copy the data
	
	dag = zeros(VARS,VARS);			% we will have VARS variables
	% Y depends on the rest:
	for y = XSIZE+1:XSIZE+YSIZE
		for x = 1:XSIZE
		  dag(x,y) = 1;
		end
	end

	% Detect the variales that are discrete:
	var_size = max([data ; testdata]);
	discrete = [];
	for i = 1:VARS
		disc = 1;
		for j = 1:ncases
			disc = disc * (round(data(j,i))==data(j,i));
		end
		if disc 
			discrete = [discrete ; i];
		else 
			var_size(i) = 1;
		end
	end
	bnet = mk_bnet(dag, var_size, 'discrete', discrete);
	for i = 1:VARS
		if ismember(i,discrete)
			bnet.CPD{i} = tabular_CPD(bnet, i);
		else
			bnet.CPD{i} = gaussian_CPD(bnet, i);
		end
	end

	engine1 = cond_gauss_inf_engine(bnet);
    disp('Learning... Continuous Agent');
	bnet2 = learn_params(bnet, cases);				% Learn using maximum likelihood
%	[bnet2, LLtrace] = learn_params_em(engine1, cases, EMIterations);	% learn using EM 
%        disp('Renormalizing...');
%	bnet2 = renormalizeDBNdistributions(bnet2);
	
%        disp('Dumping...');
%        dumpDBN(bnet2,['/Users/santi/NetBeansProjects/LFOSimulator/learned-bnets/bnet-l2c-' name '-' num2str(testtrace) '.txt'],[1,CSIZE,XSIZE,1]);
	
        disp('Testing...');
	% Now test the model:
	engine2 = cond_gauss_inf_engine(bnet2);

	for i=1:size(testdata, 1);
%	for i=1:1;
		evidence = cell(1,VARS);
		for j=1:XSIZE
			evidence{j} = testdata(i,j);
		end
		[engine1t, loglik1] = enter_evidence(engine1, evidence);
		[engine2t, loglik2] = enter_evidence(engine2, evidence);
		for j=XSIZE+1:XSIZE+YSIZE
			marg1 = marginal_nodes(engine1t, j);
			me = marg1.mu - testdata(i,j);
			total_mse1(j-XSIZE) = total_mse1(j-XSIZE) + me*me;
			
			marg2 = marginal_nodes(engine2t, j);
			me = marg2.mu - testdata(i,j);
			total_mse2(j-XSIZE) = total_mse2(j-XSIZE) + me*me;
		end
		total = total + 1;
	end
	for j = 1:YSIZE
	    disp(['Complete RND Y' num2str(j) ': ' num2str(total_mse1(j)) '/' num2str(total) ' = ' num2str(total_mse1(j)/total)]);
    	disp(['Complete LEARNED Y' num2str(j) ': ' num2str(total_mse2(j)) '/' num2str(total) ' = ' num2str(total_mse2(j)/total)]);
    end
end
randomacc = total_mse1/total;
learnedacc = total_mse2/total;