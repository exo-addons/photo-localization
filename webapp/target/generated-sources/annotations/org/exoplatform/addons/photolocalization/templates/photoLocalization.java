package org.exoplatform.addons.photolocalization.templates;
import juzu.impl.plugin.template.metadata.TemplateDescriptor;
import juzu.impl.plugin.template.TemplateService;
@javax.annotation.Generated({})
public class photoLocalization extends juzu.template.Template
{
@javax.inject.Inject
public photoLocalization(TemplateService templatePlugin)
{
super(templatePlugin, "/org/exoplatform/addons/photolocalization/templates/photoLocalization.gtmpl");
}
public static final juzu.impl.plugin.template.metadata.TemplateDescriptor DESCRIPTOR = new juzu.impl.plugin.template.metadata.TemplateDescriptor("/org/exoplatform/addons/photolocalization/templates/photoLocalization.gtmpl",0xa01b5d496d942d9cL,org.exoplatform.addons.photolocalization.templates.photoLocalization.class,juzu.impl.template.spi.juzu.dialect.gtmpl.GroovyTemplateStub.class);
public Builder builder() {
return new Builder();
}
public Builder with() {
return (Builder)super.with();
}
public class Builder extends juzu.template.Template.Builder
{
}
}
