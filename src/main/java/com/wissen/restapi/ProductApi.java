package com.wissen.restapi;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import Models.Companies;
import Models.Products;
import Utils.HibernateUtil;

@RestController
public class ProductApi {

	/*
	 * 
	 * Ramazan I��k- H�ssam Durak - Enes Ta�demir-Muharrem D�D�C� statu: fail, true,
	 * message: "", 1- T�m �r�nleri listele 2- Kategori id sine g�re �r�nleri
	 * listele 3- �r�nleri count l� listele : 0 ,10 4- kampanyal� �r�nler 5-
	 * Kampanyaya id'sine dahil olan �r�nler
	 * 
	 * Not: B�t�n �r�lerin birden fazla resmi olabilir, resmi olmama durumuda var.
	 * image:false, image:true images:[ { normal: thun: } ]
	 * 
	 */

	SessionFactory sf = HibernateUtil.getSessionFactory();

	@RequestMapping(value = "/{apiKey}/campaignsProducts/{campaignid}", method = RequestMethod.GET)
	public HashMap<String, Object> campaignsProducts(HttpServletRequest req, @PathVariable Integer campaignid,
			@PathVariable String apiKey) {
		Session sesi = sf.openSession();
		HashMap<String, Object> hm = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Companies> lc = sesi.createQuery("from Companies where companyapikey = '" + apiKey + "' ").list();
		if (lc.size() < 1) {
			hm.put("status", false);
			hm.put("message", "Company Apikey Error !");
		} else {
			try {
				@SuppressWarnings("unchecked")
				List<Products> ls = sesi.createQuery("from Products where productcompanyid = '"
						+ lc.get(0).getCompanyid() + "' and productcampaignid = '" + campaignid + "' ").list();
				hm.put("status", true);
				hm.put("message", "Products Listing Successful");
				hm.put("admins", ls);
			} catch (Exception e) {
				hm.put("status", false);
				hm.put("message", "Products Listing Failed");
				System.err.println("Hata : " + e);

			}
		}

		return hm;
	}

	@RequestMapping(value = "/{apiKey}/campaignsProductsList", method = RequestMethod.GET)
	public HashMap<String, Object> campaignsProductsList(HttpServletRequest req, @PathVariable String apiKey) {
		Session sesi = sf.openSession();
		HashMap<String, Object> hm = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<Companies> lc = sesi.createQuery("from Companies where companyapikey = '" + apiKey + "' ").list();
		if (lc.size() < 1) {
			hm.put("status", false);
			hm.put("message", "Company Apikey Error !");
		} else {
			try {
				@SuppressWarnings("unchecked")
				List<Products> ls = sesi.createQuery("from Products where productcompanyid = '"
						+ lc.get(0).getCompanyid() + "' and productcampaignid is not null ").list();
				hm.put("status", true);
				hm.put("message", "Products Listing Successful");
				hm.put("admins", ls);
			} catch (Exception e) {
				hm.put("status", false);
				hm.put("message", "Products Listing Failed");
				System.err.println("Hata : " + e);

			}
		}

		return hm;
	}
	

}
