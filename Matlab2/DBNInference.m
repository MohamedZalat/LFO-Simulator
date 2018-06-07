% Notice that I'm not considering the case that there are missing values in previous instances of time!

function [distribution] = DBNInference(bnet,engine,evidence,variable,time)

onodes = bnet.observed;
if time == 1
	% create a simple non dynamic BNET and do basic inference
	NVARS = size(bnet.dag,1);
	bnet1 = mk_bnet(bnet.dag, reshape(bnet.node_sizes,1,[]));
	classes = reshape(bnet.equiv_class,1,[]);
	for i = 1:NVARS
		bnet1.CPD{i} = bnet.CPD{classes(i)};
	end
	engine1 = jtree_inf_engine(bnet1);
	evidence1 = cell(1,NVARS);
	for i = 1:NVARS/2
		evidence1{i} = evidence{i,1};
	end
	[engine1, loglik] = enter_evidence(engine1, evidence1);
	marg = marginal_nodes(engine1,variable,1);
	distribution = marg.T;
else
	missingEvidence = 0;
    for i = 1:size(evidence,1)
		if ismember(i,onodes) && size(evidence{i,time},1) == 0
			missingEvidence = 1;
		end
	end
	if ismember(variable,onodes) == 0 && missingEvidence == 0
		% Case supported by BNT library, just use it:
		[engine2, loglik2] = enter_evidence(engine, evidence);
		marg = marginal_nodes(engine2, variable, time);
		distribution = marg.T;
	else
		% - Use standard DBN inference method to infer everything up to t-1:
		NVARS = size(bnet.dag,1);
		NVARS_SLICE = NVARS/2;
		predictions = cell(1,NVARS_SLICE);
		node_sizes = reshape(bnet.node_sizes,1,[]);
		for i = 1:NVARS_SLICE
			if ismember(i,onodes)
				predictions{i} = zeros(node_sizes(i),1);
				predictions{i}(evidence{i,time-1},1) = 1;
			else
				predictions{i} = DBNInference(bnet,engine,evidence(:,1:time-1),i,time-1);
			end
		end
		
		weighted_evidence = cell(1,2);
		weighted_evidence{1,1} = 1;
		for i = 1:NVARS_SLICE
			tmp = cell(0);
			for j = 1:node_sizes(i)
				if predictions{i}(j) ~= 0
					for k = 1:size(weighted_evidence,1)
						tmp{size(tmp,1)+1,1} = weighted_evidence{k,1} * predictions{i}(j);
						tmp{size(tmp,1),2} = [weighted_evidence{k,2} j];
					end
				end
			end
			weighted_evidence = tmp;
		end
		% - then create a non dynamic BNET to do basic inference for each of the possible scenarios (weighted by their probabilities):
		distribution = zeros(node_sizes(variable),1);
		for i = 1:size(weighted_evidence,1)
			bnet1 = mk_bnet(bnet.dag, node_sizes);
			classes = reshape(bnet.equiv_class,1,[]);
			for j = 1:NVARS
				bnet1.CPD{j} = bnet.CPD{classes(j)};
			end
			engine1 = jtree_inf_engine(bnet1);
			evidence1 = cell(1,NVARS);
			for j = 1:NVARS_SLICE
				evidence1{j} = weighted_evidence{i,2}(j);
			end
			for j = NVARS_SLICE+1:NVARS
				evidence1{j} = evidence{j-NVARS_SLICE,time};
			end
			[engine1, loglik] = enter_evidence(engine1, evidence1);
			marg = marginal_nodes(engine1,NVARS_SLICE+variable);
			tmp = struct(bnet1.CPD{NVARS_SLICE+variable});
			distribution = distribution + marg.T*weighted_evidence{i,1};
		end
	end
end
