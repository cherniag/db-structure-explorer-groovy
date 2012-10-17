package mobi.nowtechnologies.server.persistence.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import mobi.nowtechnologies.server.persistence.dao.PersistenceException;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageFrequence;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.MessageType;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserHandset;
import mobi.nowtechnologies.server.shared.dto.NewsDetailDto.UserState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The persistent class for the tb_newsDetail database table.
 * 
 */
@Entity
@Table(name="tb_newsDetail")
@NamedQueries({
	@NamedQuery(name=NewsDetail.NQ_GET_NEWS_DETAIL, query="select newsDetail from NewsDetail newsDetail where newsDetail.newsId=? and newsDetail.online=true")
})
public class NewsDetail implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(NewsDetail.class);
	
	private static final long serialVersionUID = 1L;
	
	public static final String NQ_GET_NEWS_DETAIL = "getNewsDetail"; 

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int i;

	@Column(name="item",columnDefinition="char(255)")
	private String item;
	
	@Column(name="news", insertable=false, updatable=false)
	private int newsId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="news",columnDefinition="int(10) unsigned")
	private News news;

	private byte position;

	@Lob
	@Column(name="body", nullable=false, columnDefinition="text")
	private String body;
	
	@ManyToMany(fetch=FetchType.EAGER, mappedBy="newDetails")
	private List<AbstractFilter> filters;
	
	private long timestampMilis;
	
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition="char(15)")
	private MessageType messageType;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=true, columnDefinition="char(30)")
	private MessageFrequence messageFrequence;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=true, columnDefinition="char(15)")
	private UserHandset userHandset;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=true, columnDefinition="char(50)")
	private UserState userState;
	
	private boolean online;

    public NewsDetail() {
    }

	public int getI() {
		return this.i;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getItem() {
		return this.item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public int getNewsId() {
		return newsId;
	}
	
	public News getNews() {
		return this.news;
	}

	public void setNews(News news) {
		this.news = news;
		newsId=news.getI();
	}

	public byte getPosition() {
		return this.position;
	}

	public void setPosition(byte position) {
		this.position = position;
	}
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<AbstractFilter> getFilters() {
		return filters;
	}

	public void setFiters(List<AbstractFilter> filters) {
		this.filters = filters;
	}

	public long getTimestampMilis() {
		return timestampMilis;
	}

	public void setTimestampMilis(long timestampMilis) {
		this.timestampMilis = timestampMilis;
	}
	
	public Timestamp getTimestamp() {
		return new Timestamp(timestampMilis);
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public MessageFrequence getMessageFrequence() {
		return messageFrequence;
	}

	public void setMessageFrequence(MessageFrequence messageFrequence) {
		this.messageFrequence = messageFrequence;
	}

	public UserHandset getUserHandset() {
		return userHandset;
	}

	public void setUserHandset(UserHandset userHandset) {
		this.userHandset = userHandset;
	}

	public UserState getUserState() {
		return userState;
	}

	public void setUserState(UserState userState) {
		this.userState = userState;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public NewsDetailDto toNewsDetailDto() {
		NewsDetailDto newsDetailDto = new NewsDetailDto();
		newsDetailDto.setDetail(getItem());
		newsDetailDto.setPosition(getPosition());
		newsDetailDto.setBody(getBody());

		newsDetailDto.setI(getI());
		newsDetailDto.setMessageFrequence(getMessageFrequence());
		newsDetailDto.setMessageType(getMessageType());
		//newsDetailDto.setOnline(isOnline());
		//newsDetailDto.setUserHandset(getUserHandset());
		//newsDetailDto.setUserState(getUserState());
		newsDetailDto.setTimestampMilis(getTimestampMilis());
		
		LOGGER.debug("Output parameter newsDetailDto=[{}]", newsDetailDto);
		return newsDetailDto;
	}
	
	public static List<NewsDetailDto> toNewsDetailDtos(User user, Collection<NewsDetail> newsDetails){
		if (user == null)
			throw new PersistenceException("The parameter user is null");
		if (newsDetails == null)
			throw new PersistenceException("The parameter newsDetails is null");
		
		LOGGER.debug("input parameters user, newsDetails: [{}], [{}]", user, newsDetails);
		List<NewsDetailDto> newsDetailDtos = new LinkedList<NewsDetailDto>();
		for (NewsDetail newsDetail : newsDetails) {
			final List<AbstractFilter> newDetailFilters = newsDetail.getFilters();
			boolean filtrate = true;
			for (AbstractFilter abstractFilter : newDetailFilters) {				
				filtrate = abstractFilter.doFilter(user, newsDetail);
				if (!filtrate) break;
			}
			if (filtrate) newsDetailDtos.add(newsDetail.toNewsDetailDto());
		}
		LOGGER.debug("Output parameter newsDetailDtos=[{}]", newsDetailDtos);
		return newsDetailDtos;
	}

	@Override
	public String toString() {
		return "NewsDetail [i=" + i + ", item="
				+ item + ", messageFrequence=" + messageFrequence
				+ ", messageType=" + messageType + ", newsId=" + newsId
				+ ", online=" + online + ", position=" + position
				+ ", getTimestamp()=" + getTimestamp() + ", timestampMilis="
				+ timestampMilis + ", userHandset=" + userHandset
				+ ", userState=" + userState + "]";
	}

}