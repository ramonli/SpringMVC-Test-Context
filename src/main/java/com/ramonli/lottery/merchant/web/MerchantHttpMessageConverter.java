package com.ramonli.lottery.merchant.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import com.ramonli.lottery.core.Entity;
import com.ramonli.lottery.merchant.Merchant;

/**
 * A custom <code>HttpMessageConverter</code> which used to convert message body
 * to appropriate Model type. As we don't want implement a
 * <code>HttpMessageConverter</code> for each Model type, a <Object> general
 * type is annotated.
 * <p>
 * The argument type of @RequestBody and @ResponseBody must be
 * <code>Entity</code>, and the content type must be
 * 'application/x-www-form-urlencoded'.
 * 
 * @author Ramon Li
 */
public class MerchantHttpMessageConverter implements HttpMessageConverter<Entity> {
	private Logger logger = LoggerFactory.getLogger(MerchantHttpMessageConverter.class);

	private Charset charset = Charset.forName(WebUtils.DEFAULT_CHARACTER_ENCODING);

	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		if (!Entity.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType != null) {
			return MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
		}
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		if (!Entity.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType != null) {
			return mediaType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED);
		} else {
			return true;
		}
	}

	@Override
	public List<MediaType> getSupportedMediaTypes() {
		return Arrays.asList(MediaType.APPLICATION_FORM_URLENCODED);
	}

	@Override
	public Entity read(Class<? extends Entity> clazz, HttpInputMessage inputMessage)
	        throws IOException, HttpMessageNotReadableException {
		MediaType contentType = inputMessage.getHeaders().getContentType();
		Charset charset = contentType.getCharSet() != null ? contentType.getCharSet()
		        : this.charset;
		// Get the content of request messge body
		String body = FileCopyUtils.copyToString(new InputStreamReader(inputMessage.getBody(),
		        charset));

		String[] pairs = StringUtils.tokenizeToStringArray(body, "&");

		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.add(URLDecoder.decode(pair, charset.name()), null);
			} else {
				String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
				String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
				result.add(name, value);
			}
		}
		
		if (Merchant.class.isAssignableFrom(clazz)){
			Merchant merchant = new Merchant();
			merchant.setName(result.getFirst("name"));
			merchant.setCode(result.getFirst("code"));
			merchant.setCreditLimit(new BigDecimal(result.getFirst("creditlevel")));
			
			return merchant;
		}
		else {
			throw new HttpMessageNotReadableException("Can't convert request body to " + clazz);
		}
	}

	@Override
	public void write(Entity t, MediaType contentType, HttpOutputMessage outputMessage)
	        throws IOException, HttpMessageNotWritableException {
		if (t instanceof Merchant){
			Merchant m = (Merchant)t;
			outputMessage.getHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			StringBuilder builder = new StringBuilder();
			builder.append("id=").append(m.getId()).append("&").append("name=");
			builder.append(m.getName()).append("&").append("code=").append(m.getCode());
			
			FileCopyUtils.copy(builder.toString(), new OutputStreamWriter(outputMessage.getBody(), charset));
		}
		else {
			throw new HttpMessageNotReadableException("Can't convert response body from " + t);
		}
	}

}
