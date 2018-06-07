% YDEFAULT is the default value for an action (e.g. the value for 'no-action')

function [bnet,engine] = learnBNetOrderK(traces,XSIZE,YSIZE,ORDER,YDEFAULT)

VARS = XSIZE*ORDER + YSIZE*ORDER;

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


% disp(data(1:10,:))

ncases = size(data, 1);			% number of data points
cases = cell(VARS,ncases);		% create an empty table to store the data to be given to the learning algorithm
cases(:,:) = num2cell(data(:,:)');	% copy the data
	
% readjust the XSIZE variable to make the rest of the code identical to the order 1 code:
XSIZE = VARS - YSIZE;

dag = zeros(VARS,VARS);			% we will have VARS variables
% Y depends on the rest:
for y = XSIZE+1:XSIZE+YSIZE
	for x = 1:XSIZE
	  dag(x,y) = 1;
	end
end

% Detect the variales that are discrete:
var_size = max(data);
discrete = [];
for i = 1:VARS
	disc = 1;
	for j = 1:ncases
		disc = disc * (round(data(j,i))==data(j,i));
	end
	if disc 
		discrete = [discrete ; i];
		var_size(i) = max(var_size(i),2);
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

engine = cond_gauss_inf_engine(bnet);
bnet = learn_params(bnet, cases);
%bnet = renormalizeDBNdistributions(bnet);
engine = cond_gauss_inf_engine(bnet);
