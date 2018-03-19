package com.javawissen.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mchange.v2.cfg.PropertiesConfigSource.Parse;

import Models.Admins;
import Models.Category;
import Utils.HibernateUtil;
import Utils.Utils;

@Controller
@RequestMapping (value="/admin")
public class CategoryController {
	
	// yeni kategori ekle, d�zenle, listei, silme
	// Zeki, Sema
	
	SessionFactory sf=HibernateUtil.getSessionFactory();
	//kategori listeleme
	@RequestMapping(value = "/category", method = RequestMethod.GET)
	public String categoryList(HttpServletRequest req, Model model, Admins adm) {
		Session sesi = sf.openSession();
		req.getSession().getAttribute("kid");
		Admins admin = (Admins) sesi.createQuery("from Admins a where a.aid = :kid").
				setParameter("kid",req.getSession().getAttribute("kid") ).getSingleResult();
		
		@SuppressWarnings("unchecked")
		List<Category> Cls = sesi.createQuery("from Category where categorycompanyid = :catid").setParameter("catid",(admin.getAcompanyid())  )
				.list();
		
		List<Category> ls = new ArrayList<Category>();
		sesi.close();
		for (Category item : Cls) {
			if (item.getCategoryparentid() == 0) {
				ls.add(item);
				for (Category itemx : Cls) {
					if(itemx.getCategoryparentid() != 0 && itemx.getCategoryparentid() == item.getCategoryid()) {
						ls.add(itemx);
					}
				}
			}
		}

	

	model.addAttribute("newls",ls);

	return Utils.loginControl(req,"admin/category");

	}
	
	
	@RequestMapping (value="/addCategory",method = RequestMethod.GET)
	public String categoryadd(Model model) {
		
		List<Category>showCategory=fillCategoryDropdown();
		model.addAttribute("showCategory", showCategory);
		return "admin/addCategory";
	}
	
	//kategori ekleme
	@ResponseBody
	@RequestMapping (value="/addingCategory",method = RequestMethod.POST)
	public String addingCategory(Category ct,HttpServletRequest req ) {
		System.out.println("addingCategory");
		try {
			if (ct.getCategorytitle().equals("") || ct.getCategorydescription().equals("")|| ct.getCategorysort().equals("")) {
			
				return "";
				
			}else {
			Session sesi = sf.openSession();
			
			Transaction tr = sesi.beginTransaction();
			req.getSession().getAttribute("kid");
			Admins admin = (Admins) sesi.createQuery("from Admins a where a.aid = :kid").
					setParameter("kid",req.getSession().getAttribute("kid") ).getSingleResult();
			
			ct.setCategorycompanyid((admin.getAcompanyid()) );//session dan al�nacak.req.getsession.getattribute bu kodla
			ct.setCategorylink(categoryLinkEdit(ct.getCategorytitle()));
			if (ct.getCategoryparentid() == 0) {
				ct.setCategoryparentid(0);
			}
			sesi.save(ct);
			tr.commit();
			sesi.close();
			String data = "<option value=\""+ct.getCategoryid()+"\">"+ct.getCategorytitle()+"</option>";
			return data;
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		return "";
		//return  Utils.loginControl(req, "redirect:/admin/addCategory");
	}
	
	public String categoryLinkEdit(String data) {
		
		String categoryLink = data.replace(" ", "-");
		return categoryLink;
	}
	//dropdown men� doldurma
	@SuppressWarnings("unchecked")
	public List<Category>fillCategoryDropdown(){
		List<Category>ls=new ArrayList<Category>();
		Session sesi=sf.openSession();
		ls=sesi.createQuery("from Category").list();
		sesi.close();
		return ls;
	}
	//d�zenleme
	@RequestMapping(value = "edit/{id}", method = RequestMethod.GET)
	public String editRow(@PathVariable Integer id , Model model) {
		Session sesi = sf.openSession();
		Transaction tr = sesi.beginTransaction();
		Category kl = (Category) sesi.createQuery("from Category where categoryid = '"+id+"'").list().get(0);
		tr.commit();
		sesi.close();
		List<Category>showCategory=fillCategoryDropdown();
		model.addAttribute("showCategory", showCategory);
		
		model.addAttribute("kl", kl);
		return "admin/addCategory";
	}
	
}
