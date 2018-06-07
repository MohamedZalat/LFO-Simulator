% example: [a,b] = evaluateLevel3Agent('FixedSequenceAgent','traces-straightline',7,1,8,20,20)
% example: [a,b] = evaluateLevel3Agent('SmartRandomAgent','traces-straightline',7,1,8,20,20)
% example: [a,b] = evaluateLevel3Agent('SmartStraightLineAgent','traces-straightline',7,1,8,20,20)
% example: [a,b] = evaluateLevel3Agent('ZigZagAgent','traces-straightline',7,1,8,20,20)
% example: [a,b] = evaluateLevel3Agent('SmartRandomExplorerAgent','traces-straightline',7,1,8,20,10)

function [randomacc,learnedacc] = evaluateLevel3Agent(name,prefix,nmaps,CSIZE,XSIZE,EMIterations,window)
YSIZE = 1;
STATES = 4;
traces = cell(nmaps-1,1);
for i = 1:nmaps
  traces{i} = ['trace-m' num2str(i-1) '-' name '.txt'];
end

correct1 = 0;
correct2 = 0;
total = 0;

for testtrace = 1:nmaps
	VARS = CSIZE + XSIZE + 1;					 
	
	j = 1;
	alldata = [];
	data = []; 
	cases = cell(1,nmaps-1);
	testdata = [];
	for i = 1:nmaps
		if i ~= testtrace
			data = load([prefix traces{i}]);
		    data = data(1:25,:);
			alldata = [alldata ; data];
			seqlen = size(data, 1);		% number of data points
			datac = cell(VARS,seqlen);
		%	disp(['data size: ' num2str(size(data))]);
		%	disp(['datac size: ' num2str(size(datac))]);
			datac([CSIZE+1:VARS], :) = num2cell(data');
		%	datac
			cases{j} = datac;
			j = j + 1;

		else
            tmp = load([prefix traces{i}]);
            tmp = tmp(1:25,:);
			alldata = [alldata ; tmp];
			testdata = [testdata ; tmp];
			['testing with ' traces{i}] 
		end
	end
	
	
	intra = zeros(VARS,VARS);			% we will have VARS variables
	% C depends on X:
	for c = 1:CSIZE
	  for x = 1:XSIZE
		intra(x+CSIZE,c) = 1;
	  end
	end
	% Y depends on X and C:
	for y = 1:YSIZE
		for x = 1:XSIZE
		  intra(x+CSIZE,y+CSIZE+XSIZE) = 1;
		end
		for c = 1:CSIZE
		  intra(c,y+CSIZE+XSIZE) = 1;
		end
	end 
	inter = zeros(VARS,VARS);			% we will have VARS variables
	% C depends on C-1:
	for c1 = 1:CSIZE
	  for c2 = 1:CSIZE
		inter(c1,c2) = 1;
	  end
	end
	% C depends on Y-1:
	for y = 1:YSIZE
		for c = 1:CSIZE
	  		inter(y+CSIZE+XSIZE,c) = 1;
		end
	end

	% observable nodes:	
	onodes = [CSIZE+1:VARS];

	% equivalence classes:
	eclass1 = [1:VARS];									% slice 1
	eclass2 = [VARS+1:VARS+CSIZE CSIZE+1:VARS];			% slice 2
	eclass = [eclass1 eclass2];
	eclasses = max(eclass);	
	
	var_size = [STATES max(alldata)];
	discrete = 1:VARS;
	for i = 1:VARS-1
		disc = 1;
		for j = 1:size(alldata,1)
			disc = disc * (round(alldata(j,i))==alldata(j,i));
			if alldata(j,i)<1
				disc = 0;
			end
		end
		if disc 
			if var_size(i+1) == 1
				var_size(i+1) = 2;
			end	
		else 
			var_size(i+1) = 1;
		end
	end	

	bnet = mk_dbn(intra, inter, var_size, 'discrete', discrete, 'eclass1', eclass1, 'eclass2', eclass2, 'observed', onodes);	
	for i = 1:eclasses
		bnet.CPD{i} = tabular_CPD(bnet, i);
	end

	engine1 = smoother_engine(jtree_2TBN_inf_engine(bnet));
    disp(['Learning...' num2str(size(cases,2)) 'x' num2str(size(cases{1},1)) 'x' num2str(size(cases{1},2))]);
    disp(['EMIterations: ' num2str(EMIterations)])
	[bnet2, LLtrace] = learn_params_dbn_em(engine1, cases, 'max_iter', EMIterations);
%	bnet2 = renormalizeDBNdistributions(bnet2);
	
%        disp('Dumping...');
%	dumpDBN(bnet2,['C:\my-research\FullBNT-1.0.7\santi-experiments\learned-bnets\bnet-simple-l3-' name '-' num2str(testtrace) '.txt'],[2,CSIZE,XSIZE,1]);
		
	% Now test the model:
	engine2 = smoother_engine(jtree_2TBN_inf_engine(bnet2));

	for i=1:size(testdata, 1);
		total = total + 1;

                % TEST FIRST WITH THE WINDOW:
%		evidence = cell(VARS,min(window,i));	
%		start = max(1,i-window);
%		amount = min(window,i);
%		for t=1:amount-1
%			for j=CSIZE+1:CSIZE+XSIZE+1
%				evidence{j,t} = testdata(t+start-1,j);
%			end
%		end
%		for j=CSIZE+1:CSIZE+XSIZE
%			evidence{j,amount} = testdata(i,j);
%		end
%		marg1 = DBNInference(bnet2,engine2,evidence,CSIZE+XSIZE+1, amount);
%		prediction1 = argmax(marg1);
%		if prediction1 == testdata(i,CSIZE+XSIZE+1)
%			correct1 = correct1 + 1;
%		end        
%		disp(['Evaluating time step (window) ' num2str(i) ' -> ' num2str(prediction1) ' == ' num2str(testdata(i,CSIZE+XSIZE+1)) ' ? -> ' num2str(correct1) '/' num2str(total) ' = ' num2str(correct1/total)]);
        
                % NOW WITH THE WHOLE EVIDENCE:
		evidence = cell(VARS,i);		% we set all the evidence we have form the beginning of the trace till the current point:
		start = 1;
		amount = i;
		for t=1:amount-1
			for j=CSIZE+1:CSIZE+XSIZE+1
				evidence{j,t} = testdata(t+start-1,j-1);
			end
		end
		for j=CSIZE+1:CSIZE+XSIZE
			evidence{j,amount} = testdata(i,j-1);
		end
		marg2 = DBNInference(bnet2,engine2,evidence,CSIZE+XSIZE+1, amount);
		prediction2 = argmax(marg2);
		if prediction2 == testdata(i,CSIZE+XSIZE)
			correct2 = correct2 + 1;
		end
%		evidence
		disp(['Evaluating time step (all) ' num2str(i) ' -> ' num2str(marg2') ': ' num2str(prediction2) ' == ' num2str(testdata(i,CSIZE+XSIZE)) ' ? -> ' num2str(correct2) '/' num2str(total) ' = ' num2str(correct2/total)]);
	end
        disp(['Complete (window): ' num2str(correct1) '/' num2str(total) ' = ' num2str(correct1/total)]);
        disp(['Complete (all)(: ' num2str(correct2) '/' num2str(total) ' = ' num2str(correct2/total)]);
end
randomacc = correct1/total;
learnedacc = correct2/total;