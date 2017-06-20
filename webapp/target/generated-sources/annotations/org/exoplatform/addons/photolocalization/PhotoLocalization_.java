package org.exoplatform.addons.photolocalization;
import juzu.impl.request.ControllerHandler;
import juzu.impl.request.ControlParameter;
import juzu.impl.request.PhaseParameter;
import juzu.impl.request.ContextualParameter;
import juzu.impl.request.BeanParameter;
import juzu.impl.common.Tools;
import java.util.Arrays;
import juzu.request.Phase;
import juzu.impl.plugin.controller.descriptor.ControllerDescriptor;
import javax.annotation.Generated;
import juzu.impl.common.Cardinality;
import juzu.impl.request.Request;
@Generated(value={})
public class PhotoLocalization_ {
private static final Class<org.exoplatform.addons.photolocalization.PhotoLocalization> TYPE = org.exoplatform.addons.photolocalization.PhotoLocalization.class;
private static final ControllerHandler<Phase.View> method_0 = new ControllerHandler<Phase.View>("PhotoLocalization.index",Phase.VIEW,TYPE,Tools.safeGetMethod(TYPE,"index",juzu.request.RequestContext.class), Arrays.<ControlParameter>asList(new ContextualParameter("context",juzu.request.RequestContext.class)));
public static Phase.View.Dispatch index() { return Request.createViewDispatch(method_0); }
public static final ControllerDescriptor DESCRIPTOR = new ControllerDescriptor(TYPE,Arrays.<ControllerHandler<?>>asList(method_0));
}
