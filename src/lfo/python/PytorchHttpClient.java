package lfo.python;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class PytorchHttpClient {
    private URL trainUrl;
    private URL predictUrl;
    private URL resetUrl;

    public PytorchHttpClient(String trainUrlString, String predictUrlString,
                             String resetUrlString){
        try {
            this.trainUrl = new URL(trainUrlString);

            this.predictUrl = new URL(predictUrlString);

            this.resetUrl = new URL(resetUrlString);
        } catch (MalformedURLException e) {
            System.out.println("URL is invalid.");
            e.printStackTrace();
        }

    }

    private static String convertStringListToJsonString(String key, List<String> list) {
        String jsonInputString = "{\"" + key + "\" : [";

        String result = list.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(", "));

        // Make sure all the slashes are forward slashes to not malform the json.
        result = result.replace("\\", "/");

        jsonInputString += result;

        jsonInputString += "]}";
        return jsonInputString;
    }

    private static String convertIntListToJsonString(String key, List<Integer> list) {
        String jsonInputString = "{\"" + key + "\" : ";
        jsonInputString += list.toString();
        jsonInputString += "}";
        return jsonInputString;
    }

    public boolean train(List<String> fileNames) {
        HttpURLConnection conTrain = null;
        try {
            conTrain = (HttpURLConnection) trainUrl.openConnection();
            conTrain.setRequestMethod("POST");
            conTrain.setRequestProperty("Content-Type", "application/json; utf-8");
            conTrain.setRequestProperty("Accept", "application/json");
            conTrain.setDoOutput(true);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(conTrain == null) {
            return false;
        }

        String jsonInputString = convertStringListToJsonString("filenames", fileNames);

        System.out.println(jsonInputString);

        try(OutputStream os = conTrain.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conTrain.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            conTrain.disconnect();
            return response.toString().contains("Success");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        conTrain.disconnect();
        return false;
    }

    public int predict(List<Integer> X) {
        HttpURLConnection conPredict = null;
        try {
            conPredict = (HttpURLConnection) predictUrl.openConnection();
            conPredict.setRequestMethod("POST");
            conPredict.setRequestProperty("Content-Type", "application/json; utf-8");
            conPredict.setRequestProperty("Accept", "application/json");
            conPredict.setDoOutput(true);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (conPredict == null) {
            return -1;
        }

        String jsonInputString = convertIntListToJsonString("data", X);

        try(OutputStream os = conPredict.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conPredict.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            if (response.toString().contains("action")) {
                String actionString = response.toString().replaceAll("\\D+", "");
                int action = Integer.parseInt(actionString);
                conPredict.disconnect();
                return action;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conPredict.disconnect();
        return -1;
    }

    public boolean reset() {
        HttpURLConnection conReset = null;
        try {
            conReset = (HttpURLConnection) resetUrl.openConnection();
            conReset.setRequestMethod("POST");
            conReset.setRequestProperty("Content-Type", "application/json; utf-8");
            conReset.setRequestProperty("Accept", "application/json");
            conReset.setDoOutput(true);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (conReset == null) {
            return false;
        }

        String jsonInputString = "";

        try(OutputStream os = conReset.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(conReset.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            conReset.disconnect();
            return response.toString().contains("Success");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conReset.disconnect();
        return false;
    }

}
