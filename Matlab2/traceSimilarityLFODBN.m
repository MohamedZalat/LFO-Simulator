%	a = traceSimilarityLFODBN(['traces-fourraydistance/trace-m0-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m1-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m2-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m3-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m4-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m5-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m6-FixedSequenceAgent.txt'], ['traces-fourraydistance/trace-m0-WallFollowerAgent.txt';'traces-fourraydistance/trace-m1-WallFollowerAgent.txt';'traces-fourraydistance/trace-m2-WallFollowerAgent.txt';'traces-fourraydistance/trace-m3-WallFollowerAgent.txt';'traces-fourraydistance/trace-m4-WallFollowerAgent.txt';'traces-fourraydistance/trace-m5-WallFollowerAgent.txt';'traces-fourraydistance/trace-m6-WallFollowerAgent.txt'], 10,4,8,1)

function [globallogsym] = traceSimilarityLFODBN(engine1,engine2,traces1, traces2,XSIZE,YSIZE)

% Prepare the data for similarity:
CSIZE = 1;
VARS = CSIZE+XSIZE+YSIZE;
alldata1 = [];
data1 = [];
cases1 = cell(1,size(traces1,1));
for i = 1:size(traces1,1)
	data1 = load(traces1(i,:));
%    data1 = data1(1:2,:);
	alldata1 = [alldata1 ; data1];
	seqlen = size(data1, 1);		% number of data points
	datac = cell(VARS,seqlen);
	datac([CSIZE+1:VARS], :) = num2cell(data1');
	cases1{i} = datac;
end

alldata2 = [];
data2 = [];
cases2 = cell(1,size(traces2,1));
for i = 1:size(traces2,1)
	data2 = load(traces2(i,:));	
%    data2 = data2(1:2,:);
	alldata2 = [alldata2 ; data2];
	seqlen = size(data2, 1);		% number of data points
	datac = cell(VARS,seqlen);
	datac([CSIZE+1:VARS], :) = num2cell(data2');
	cases2{i} = datac;
end


globallogsym = 0;
for i = 1:size(traces1,1)
	for j = 1:size(traces2,1)
%		disp(['Similarity of : ' num2str(i) ' and ' num2str(j) '...']);
		try
			[engine11_tmp, loglik11] = enter_evidence(engine1, cases1{i});
		catch err
			loglik11 = -1000000;
		end
%		disp(['ll11: ' num2str(loglik11)]);
		try
  			[engine12_tmp, loglik12] = enter_evidence(engine1, cases2{j});
		catch err
			loglik12 = -1000000;
		end
%		disp(['ll12: ' num2str(loglik12)]);
		try
			[engine21_tmp, loglik21] = enter_evidence(engine2, cases1{i});
		catch err
			loglik21 = -1000000;
		end
%		disp(['ll21: ' num2str(loglik21)]);
		try
			[engine22_tmp, loglik22] = enter_evidence(engine2, cases2{j});
		catch err
			loglik22 = -1000000;
		end
%		disp(['ll22: ' num2str(loglik22)]);
		logsym = (loglik12 + loglik21) - (loglik11 + loglik22);
%		disp(['Similarity of : ' num2str(i) ' and ' num2str(j) ' = ' num2str(logsym)]);
		globallogsym = globallogsym + logsym;
	end
end
globallogsym;
