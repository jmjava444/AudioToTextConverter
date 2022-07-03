/**
 * Author: Joshua McKenzie
 * Date: 7/3/2022
 * Credit: I completed this code following a tutorial by "Coding with John" on YouTube, and you can check out
 * his great videos here https://www.youtube.com/c/CodingwithJohn/ Also, this code uses https://assemblyai.com/
 * by utilizing their wonderful REST API that converts audio into text.
 *
 * DESCRIPTION:
 * You can plug in a URL link to any audio file with words in it, and AssemblyAI will convert it into a readable
 * text with or without punctuation. There are various options for you to delve into, and this project only
 * covers the basics. I added the output to file at the end of this code so that I can keep the text files
 * after executing the program.
 *
 * NOTE: You need to have an API key provided by AssemblyAI, and all you need to do is sign up for an account
 * for free with them to gain access to the Basic Plan.
 */

package com.josh_mckenzie.audio_to_text_converter;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AudioToTextConverter
{
	
	public static void main(String[] args) throws Exception
	{
		// create a transcript object which we can read/write data values to
		Transcript transcript = new Transcript();
		// MP3 URL is hardcoded here, so change it if you want to sample a different audio byte
		transcript.setAudio_url("http://josh-mckenzie.com/wp-content/uploads/2022/07/REST-API-Test-Audio-Recording.mp3");
		// Gson reads/writes to our transcript object
		Gson gson = new Gson();
		String jsonRequest = gson.toJson(transcript);
		System.out.println("JSON Request: " + jsonRequest);
		// Build the POST request
		HttpRequest postRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript"))
				.header("authorization", Constants.API_KEY) // You will need to insert your own API Key
				.POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
				.build();
		// Create a new HTTP client
		HttpClient httpClient = HttpClient.newHttpClient();
		// Send our HTTP request, and save it to postResponse
		HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
		System.out.println(postResponse.body());
		// Convert the postResponse from JSON back into our Transcript object
		transcript = gson.fromJson(postResponse.body(), Transcript.class);
		System.out.println(transcript.getId());
		// Build the GET request
		HttpRequest getRequest = HttpRequest.newBuilder()
				.uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
				.header("authorization", "14acf03c1ff54102bcd5fca4249263bc")
				.GET()
				.build();
		
		int timeTakenInSeconds = 0;
		while(true)
		{
			// Send a GET request every second to check if our audio is done processing
			HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
			// Saves the current body from the GET request to our transcript object
			transcript = gson.fromJson(getResponse.body(), Transcript.class);
			// Print the status every second
			System.out.println(transcript.getStatus() + " for " + timeTakenInSeconds + " seconds");
			if("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus()))
			{
				break;
			}
			Thread.sleep(1000);
			timeTakenInSeconds++;
		}
		
		// Show that it completed and show text in the console
		System.out.println("Transcription completed!");
		System.out.println("The audio converted to text is: \"" + transcript.getText() + "\"");
		
		// Create a date formatter for unique file names
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");
		LocalDateTime now = LocalDateTime.now();
		// Create a file to save the transcript to
		File file = new File(dtf.format(now) + " Audio-to-Text-" + timeTakenInSeconds + "s.txt");
		// Print the output of the transcript to the file
		PrintWriter output = new PrintWriter(file);
		output.print(transcript.getText());
		output.close();
	}
}
