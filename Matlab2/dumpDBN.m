function dumpDBN(bnet,fileName,header)

NVARS = size(bnet.dag,1);
dlmwrite(fileName,header);
dlmwrite(fileName,bnet.dag,'-append');
eclass = reshape(bnet.equiv_class,1,[]);
node_sizes = reshape(bnet.node_sizes,1,[]);
dlmwrite(fileName,node_sizes,'-append');
for i = 1:NVARS
	a = struct(bnet.CPD{eclass(i)});
	flattened = reshape(a.CPT,1,[]);
	tmp = 1;
	for j = 1:NVARS
		if bnet.dag(j,i) == 1
			tmp = tmp*node_sizes(j);
		end
	end
	if size(flattened,2) ~= tmp*node_sizes(i)
		disp(['TDP has ' num2str(size(flattened,2)) ' entries, and it should have ' num2str(tmp) ' * ' num2str(node_sizes(i)) ' = ' num2str(tmp*node_sizes(i))]);
	end
	for j = 1:tmp
		tmp2 = 0;
		for k = 1:node_sizes(i)
			tmp2 = tmp2 + flattened(j+(k-1)*tmp);
		end
%		if tmp2 ~= 1
%			disp([num2str(tmp2) ' should be 1']);
%		end
	end
	dlmwrite(fileName,flattened,'-append');
end