package mobi.nowtechnologies.server.user.feedback.pages;

/**
 * Author: Gennadii Cherniaiev
 * Date: 4/8/2014
 */
public class FeedBackPagesServiceConfig {

    private FeedBackPagesService feedBackPagesService;

    private FeedBackPagesServiceConfig(){
        feedBackPagesService = new FeedBackPagesService();
    }

    public static FeedBackPagesServiceConfig newConfig(){
        return new FeedBackPagesServiceConfig();
    }

    public FeedBackPagesServiceConfig addRule(){
        return this;
    }



}
