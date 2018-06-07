function [logprob] = traceProbabilityGivenModel(traceFile, dbn, engine, XSIZE,YSIZE);

% Prepare the data for similarity:
CSIZE = 1;
VARS = CSIZE+XSIZE+YSIZE;
data1 = load(traceFile);
seqlen = size(data1, 1);		% number of data points
datac = cell(VARS,seqlen);
datac([CSIZE+1:VARS], :) = num2cell(data1');

[engine_tmp, ll] = enter_evidence(engine, datac);

logprob = ll;
