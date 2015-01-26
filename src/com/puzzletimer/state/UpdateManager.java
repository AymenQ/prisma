package com.puzzletimer.state;

import static com.puzzletimer.Internationalization._;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class UpdateManager {

    public static boolean checkUpdate()
    {
        try {
            if (normalisedVersion(getLatest().getString("tag_name"), ".", 3).compareTo(normalisedVersion(_("about.version"), ".", 3)) > 0)
                return true;
            return false;
        }
        catch(JSONException e)
        {

        }
        return false;
    }

    public static JSONObject getLatest()
    {

        try {
            JSONArray releases = readJsonFromUrl("https://api.github.com/repos/Moony22/prisma/releases");
            String latest = "";
            JSONObject latestRelease = releases.getJSONObject(0);
            for(int i = 0; i < releases.length(); i++)
            {
                JSONObject release = releases.getJSONObject(i);
                String releaseTag = release.getString("tag_name");
                if(releaseTag.compareTo(latest) > 0) {
                    latest = releaseTag;
                    latestRelease = release;
                }
            }
            return latestRelease;
        }
        catch(Exception e)
        {

        }

        return new JSONObject();

    }

    public static String getVersionNumber(JSONObject release)
    {
        try {
            String body = release.getString("tag_name");
            return body;
        }
        catch (JSONException e)
        {

        }

        return "";
    }

    public static String getDescription(JSONObject release)
    {
        try {
            String body = release.getString("body");
            return body;
        }
        catch (JSONException e)
        {

        }

        return "";
    }


    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONArray json = new JSONArray(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

}
