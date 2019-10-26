package amata1219.parkour.tweet;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;

public class IntentTweetBuilder {

	private final String text;
	private final List<String> hashtags = new ArrayList<>();
	private String url;

	public IntentTweetBuilder(String text){
		this.text = text;
	}

	public IntentTweetBuilder addHashtag(String hashtag){
		hashtags.add(hashtag);
		return this;
	}

	public IntentTweetBuilder setURL(String url){
		this.url = url;
		return this;
	}

	public String build(){
		String text = "text=" + encode(this.text);
		String hashtags = this.hashtags.isEmpty() ? "" : "&hashtags=" + encode(String.join(",", this.hashtags));
		String url = this.url != null ? "&url=" + this.url : "";
		return "https://twitter.com/intent/tweet?" + text + hashtags + url;
	}

	private String encode(String text){
		try {
			return new URLCodec("UTF-8").encode(text);
		} catch (EncoderException e) {
			e.printStackTrace();
		}
		return "";
	}

}
