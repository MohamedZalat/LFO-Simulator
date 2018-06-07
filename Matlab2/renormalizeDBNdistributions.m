function [bnet2] = renormalizeDBNdistributions(bnet)

NVARS = size(bnet.dag,1);
eclass = reshape(bnet.equiv_class,1,[]);
node_sizes = reshape(bnet.node_sizes,1,[]);
for i = 1:NVARS
	a = struct(bnet.CPD{eclass(i)});
	flattened = reshape(a.CPT,1,[]);
	tmp = 1;
	for j = 1:NVARS
		if bnet.dag(j,i) == 1
			tmp = tmp*node_sizes(j);
		end
	end
	for j = 1:tmp
		total = 0;
		for k = 1:node_sizes(i)
			total = total + flattened(j+(k-1)*tmp);
		end
		if total == 0
			for k = 1:node_sizes(i)
				flattened(j+(k-1)*tmp) = 1/node_sizes(i);
			end
		else
			for k = 1:node_sizes(i)
				flattened(j+(k-1)*tmp) = flattened(j+(k-1)*tmp)/total;
			end			
		end
	end
	bnet.CPD{eclass(i)} = tabular_CPD(bnet, eclass(i), flattened);
end
bnet2 = bnet
