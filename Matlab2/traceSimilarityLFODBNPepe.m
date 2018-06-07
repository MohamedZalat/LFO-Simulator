%	a = traceSimilarityLFODBN(['traces-fourraydistance/trace-m0-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m1-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m2-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m3-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m4-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m5-FixedSequenceAgent.txt';'traces-fourraydistance/trace-m6-FixedSequenceAgent.txt'], ['traces-fourraydistance/trace-m0-WallFollowerAgent.txt';'traces-fourraydistance/trace-m1-WallFollowerAgent.txt';'traces-fourraydistance/trace-m2-WallFollowerAgent.txt';'traces-fourraydistance/trace-m3-WallFollowerAgent.txt';'traces-fourraydistance/trace-m4-WallFollowerAgent.txt';'traces-fourraydistance/trace-m5-WallFollowerAgent.txt';'traces-fourraydistance/trace-m6-WallFollowerAgent.txt'], 10,4,8,1)

function [globallogsym] = traceSimilarityLFODBNPepe(engine_learningagent, traces2_expert,N, XSIZE,YSIZE)

% Prepare the data for similarity:
CSIZE = 1;
VARS = CSIZE+XSIZE+YSIZE;
alldata1 = [];
data1 = [];
cases1 = cell(1,size(traces2_expert,1));
for i = 1:size(traces2_expert,1)
	data1 = load(traces2_expert(i,:));
%    data1 = data1(1:2,:);
	alldata1 = [alldata1 ; data1];
	seqlen = size(data1, 1);		% number of data points
	datac = cell(VARS,seqlen);
	datac([CSIZE+1:VARS], :) = num2cell(data1');
	cases1{i} = datac;
end

alldata2 = [];
data2 = [];
cases2 = cell(1,size(traces2_expert,1));
for i = 1:size(traces2_expert,1)
	data2 = load(traces2_expert(i,:));	
%    data2 = data2(1:2,:);
	alldata2 = [alldata2 ; data2];
	seqlen = size(data2, 1);		% number of data points
	datac = cell(VARS,seqlen);
	datac([CSIZE+1:VARS], :) = num2cell(data2');
	cases2{i} = datac;
end


globallogsym = 0;
ntraces = 0;
for i = 1:size(traces2_expert,1)
	trace = cases1{i};
%	for j = 1:size(trace,2)-N
		try
			[engine21_tmp, loglik21] = enter_evidence(engine_learningagent, trace(:,1:N));
		catch err
			loglik21 = -1000000;
		end
		globallogsym = globallogsym + loglik21/N;
		ntraces  = ntraces + 1;
%	end
end
globallogsym/ntraces;
