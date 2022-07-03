package com.josh_mckenzie.audio_to_text_converter;

// Gson uses this class that I made to read/write the values of interest for using the text-to-speech API
public class Transcript
{
	private String audio_url;
	private String id; // The unique identifier of your transcription
	private String status; // possible answers include: queued, processing, completed, or error
	private String text; // The text that was transcribed from audio
	
	public String getAudio_url()
	{
		return audio_url;
	}
	
	public void setAudio_url(String audio_url)
	{
		this.audio_url = audio_url;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
}
