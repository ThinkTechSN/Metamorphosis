package org.metamorphosis.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.digester.Digester;

public class TemplateManager implements TemplateParser {

	private Map<String,Template> templates = new HashMap<String,Template>();
	private Logger logger = Logger.getLogger(TemplateManager.class.getName());
	private static TemplateManager instance;
	private static final String TEMPLATE_METADATA = "template.xml";

	public TemplateManager() {
		instance = this;
	}

	public void loadTemplates(File folder) {
		File[] files = folder.listFiles();
		if(files!=null) {
			for(File file : files) {
				if(file.isDirectory()) {
					try {
						loadTemplate(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			monitorFolder(folder);
		}
	}

	public Template loadTemplate(File folder) throws Exception {
		File metadata = new File(folder+"/"+TEMPLATE_METADATA);
		Template template = metadata.exists() ? createParser().parse(metadata) : new Template();
		template.setFolder(folder);
		addTemplate(template);
		return template;
	}
	
	private TemplateParser createParser() {
		TemplateParser parser = this;
		return parser;
	}

	public Template parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("template", Template.class);
		digester.addBeanPropertySetter("template/id");
		digester.addBeanPropertySetter("template/name");
		digester.addBeanPropertySetter("template/index");
		digester.addBeanPropertySetter("template/type");
		digester.addBeanPropertySetter("template/selected");
		digester.addBeanPropertySetter("template/author");
		digester.addBeanPropertySetter("template/authorEmail");
		digester.addBeanPropertySetter("template/authorUrl");
		digester.addBeanPropertySetter("template/description");
		digester.addBeanPropertySetter("template/details");
		digester.addBeanPropertySetter("template/creationDate");
		digester.addBeanPropertySetter("template/copyright");
		digester.addBeanPropertySetter("template/license");
		digester.addBeanPropertySetter("template/version");
		return (Template) digester.parse(metadata);
	}

	private void monitorFolder(final File folder) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			new FileMonitor(folder).addListener(new FileListener() {
				public void onFileCreated(String name) {
					File file = new File(folder+"/"+name);
					if(file.isDirectory()) {
						logger.log(Level.INFO, "adding template from folder : " + name);
						addTemplate(new Template(file));
					}
				}
				public void onFileDeleted(String name) {
					Collection<Template> templates = getTemplates();
					for(Template template : templates){
						if(template.getFolder().getName().equals(name)) {
							logger.log(Level.INFO, "removing template from folder : " + name);
							removeTemplate(template);
							break;
						}
					}
				}	
			}).monitor();
		}
	}

	public void addTemplate(Template template) {
		monitorTemplate(template);
		templates.put(template.getId(),template);
	}
	
	private void monitorTemplate(final Template template) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			new FileMonitor(template.getFolder()).addListener(new FileListener() {
				public void onFileCreated(String name) {
					if(name.equals(TEMPLATE_METADATA)) updateTemplate(template);
				}
				public void onFileDeleted(String name) {
				}
			}).monitor();
		}
	}

	private void updateTemplate(Template template) {
		try {
			logger.log(Level.INFO, "updating template from folder : " + template.getFolder().getName());
			String id = template.getId();
			File folder = template.getFolder();
			template = parse(new File(folder+"/"+TEMPLATE_METADATA));
			template.setFolder(folder);
			templates.remove(id);
			templates.put(template.getId(),template);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeTemplate(Template template) {
		templates.remove(template.getId());
	}

	public Template getTemplateById(String id) {
		Collection<Template> templates = getTemplates();
		for(Template template : templates) if(template.getId().equals(id)) return template;
		return null;
	}
	
	public Template getTemplateByName(String name) {
		Collection<Template> templates = getTemplates();
		for(Template template : templates) if(template.getName().toLowerCase().equals(name.toLowerCase())) return template;
		return null;
	}

	public Template getBackendTemplate(String id) {
		Collection<Template> templates = getTemplates();
		Template template = getTemplateById(id);
		if(template != null && template.isBackend()) return template;
		for(Template current : templates) if(current.isSelected() && current.isBackend()) return current;
		for(Template current : templates) if(current.isBackend()) return current;
		return null;
	}

	public Template getFrontendTemplate(String id) {
		Collection<Template> templates = getTemplates();
		Template template = getTemplateById(id);
		if(template != null && template.isFrontend()) return template;
		for(Template current : templates) if(current.isSelected() && current.isFrontend()) return current;
		for(Template current : templates) if(current.isFrontend()) return current;
		return null;
	}

	public Collection<Template> getTemplates() {
		return templates.values();
	}
	
	public Collection<Template> getBackendTemplates() {
		List<Template> list = new ArrayList<Template>();
		Collection<Template> templates = getTemplates();
		for(Template current : templates) if(current.isBackend()) list.add(current);
		Collections.sort(list);
		return list;
	}
	
	public Collection<Template> getFrontendTemplates() {
		List<Template> list = new ArrayList<Template>();
		Collection<Template> templates = getTemplates();
		for(Template current : templates) if(current.isFrontend()) list.add(current);
		Collections.sort(list);
		return list;
	}

	public static TemplateManager getInstance() {
		return instance;
	}

}