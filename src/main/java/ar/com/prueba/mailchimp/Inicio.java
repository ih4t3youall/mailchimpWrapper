package ar.com.prueba.mailchimp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import com.ecwid.mailchimp.MailChimpClient;
import com.ecwid.mailchimp.MailChimpException;
import com.ecwid.mailchimp.MailChimpObject;
import com.ecwid.mailchimp.method.v1_3.campaign.CampaignCreateMethod;
import com.ecwid.mailchimp.method.v1_3.campaign.CampaignSendNowMethod;
import com.ecwid.mailchimp.method.v1_3.campaign.CampaignSendTestMethod;
import com.ecwid.mailchimp.method.v1_3.campaign.CampaignType;
import com.ecwid.mailchimp.method.v1_3.template.TemplateInformation;
import com.ecwid.mailchimp.method.v1_3.template.TemplatesMethod;
import com.ecwid.mailchimp.method.v1_3.template.TemplatesMethodInactives;
import com.ecwid.mailchimp.method.v1_3.template.TemplatesMethodTypes;
import com.ecwid.mailchimp.method.v1_3.template.TemplatesResult;
import com.ecwid.mailchimp.method.v2_0.lists.Email;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethod;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethodResult;
import com.ecwid.mailchimp.method.v2_0.lists.ListMethodResult.Data;
import com.google.gson.Gson;
import com.sun.javafx.animation.TickCalculation;
import com.ecwid.mailchimp.method.v2_0.lists.SubscribeMethod;

public class Inicio {
	
	private final String API = "19f90103fbad35fcc934b500404d14d6-us14";
	private final String LIST = "c49405bac5";
	private MailChimpClient mailChimpClient = new MailChimpClient();
	
	public Inicio() throws IOException, MailChimpException{
		//addSubscriptor();
		createCampaign("Template", "Listita");
	}
	
	public TemplateInformation getTemplateInfo(String templateName) throws IOException, MailChimpException{
		
		TemplatesMethodInactives tmi = new TemplatesMethodInactives();
		tmi.include=false;
		tmi.only=false;
		
		TemplatesMethodTypes tmt = new TemplatesMethodTypes();
		tmt.user=true;
		tmt.gallery=false;
		tmt.base=false;
		
		TemplatesMethod tm = new TemplatesMethod();
		tm.apikey = API;
		tm.inactives = tmi;
		tm.types = tmt;
		
		TemplatesResult tr = mailChimpClient.execute(tm);
		
		for(TemplateInformation ti : tr.user){
			if(ti.name.equals(templateName)) return ti;
		}	
		return null;
		
	}
	
	public Data getList(String listName) throws IOException, MailChimpException{
		ListMethod listMethod = new ListMethod();
        listMethod.apikey = API;
        ListMethodResult listResult = mailChimpClient.execute(listMethod);
        for(Data list : listResult.data){
			if(list.name.equals(listName)) return list;
		}
        return null;
        
	}
	
	public void createCampaign(String templateName, String listName) throws IOException, MailChimpException{
		
        try{
        CampaignCreateMethod campaignCreateMethod = new CampaignCreateMethod();

        campaignCreateMethod.apikey = API;
        campaignCreateMethod.type = CampaignType.regular;
        
        MailChimpObject options = new MailChimpObject();

        campaignCreateMethod.options = new MailChimpObject();
        options.put("list_id", getList(listName).id);
        options.put("subject", "Hello");
        options.put("from_email", "sombra571@hotmail.com");
        options.put("from_name", "Monolot");
        options.put("template_id", getTemplateInfo(templateName).id);
        options.put("authenticate", true);
        options.put("title", "Campaign: "+ templateName + " " + listName);
        options.put("inline_css", true);
        options.put("generate_text", true);
        campaignCreateMethod.options = options;

        MailChimpObject content = new MailChimpObject();

        content.put("html_draw", "22248");
        content.put("html_game", "Lottery West");
        content.put("html_result", "3-14-27-31-56-87");

        
        campaignCreateMethod.content = content;
        System.out.println(campaignCreateMethod.toJson());
        String campaignId = mailChimpClient.execute(campaignCreateMethod);
        
        System.out.println("Created campaign ID:" + campaignId);
        sendCampaign(campaignId);
        }catch(Exception ex) {
            System.out.println(ex);
        }
	}
	
	public void sendCampaign(String campaignId) throws IOException, MailChimpException{
		
		CampaignSendNowMethod campaign = new CampaignSendNowMethod();
		campaign.apikey = API;
		campaign.cid = campaignId;
		Boolean execute = mailChimpClient.execute(campaign);
		System.out.println(LocalDateTime.now().getHour()+":"+LocalDateTime.now().getMinute() + (execute ? " Campaign sent":" Campaign Failed"));
	}
	
	public void sendCampaignTest(String campaignId) throws IOException, MailChimpException{
		
		CampaignSendTestMethod test = new CampaignSendTestMethod();
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("qwerty.001@hotmail.com");
		
		test.apikey = API;
		test.cid=campaignId;
		test.test_emails =arrayList;
			
		Boolean execute = mailChimpClient.execute(test);
		
		System.out.println(execute ? "Test Campaign sent":"Campaign Failed");
		
	}
	
	public void addSubscriptor() throws IOException, MailChimpException{
		
		String listId = LIST;
		SubscribeMethod subscribeMethod = new SubscribeMethod();
		
		subscribeMethod.apikey=API;
		subscribeMethod.id = listId;
		subscribeMethod.email = new Email();
		subscribeMethod.email.email ="marios@hotmail.com";
		subscribeMethod.double_optin = false;
		subscribeMethod.update_existing = true;
		Email execute = mailChimpClient.execute(subscribeMethod);
		System.out.println("Subscriptor added");
	}
}
