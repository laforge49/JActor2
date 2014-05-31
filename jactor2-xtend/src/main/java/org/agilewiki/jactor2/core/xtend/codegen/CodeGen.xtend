package org.agilewiki.jactor2.core.xtend.codegen

import org.eclipse.xtend.lib.macro.Active
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.TransformationContext
import java.util.List
import java.lang.annotation.Target
import java.lang.annotation.ElementType
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Retention
import org.eclipse.xtend.lib.macro.declaration.Visibility
import org.agilewiki.jactor2.core.requests.SyncRequest
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.reactors.Reactor

/** Marks an instance method that should be wrapped in a SyncRequest */
@Active(typeof(SReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation SReq {
}

/** Marks an instance method that should be wrapped in a AsyncRequest */
@Active(typeof(AReqProcessor))
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
annotation AReq {
}

/**
 * Generate the method returning a SyncRequest for an instance method.
 *
 * Example:
 *
 * <code>
    // User written
    @SReq
    private long ping() {
        count += 1;
        return count;
    }

    // Generated!
    public SyncRequest<Long> pingSReq() {
        return new SyncBladeRequest<Long>() {
            @Override
            public Long processSyncRequest() {
                return ping();
            }
        };
    }
    </code>
 *
 * @author monster
 */
class SReqProcessor
      implements TransformationParticipant<MutableMethodDeclaration> {

      	var TypeReference reactor

  override doTransform(List<? extends MutableMethodDeclaration> methods,
                       extension TransformationContext context) {
    for (m : methods) {
    	val type = m.declaringType
    	val name = m.simpleName
    	val fqName = type.qualifiedName+"."+name
    	if (type instanceof MutableClassDeclaration) {
	    	val params = m.parameters
	    	val retType = m.returnType
	    	val genName = name+"SReq"
	    	val TypeReference retTypeObj = retType.wrapperIfPrimitive;
    		type.addMethod(genName, [
		        visibility = Visibility.PUBLIC
		        final = true
		        static = false
		        returnType = context.newTypeReference(SyncRequest, retTypeObj)
		        var params2 = ""
		        for (p : params) {
		        	addParameter(p.simpleName, p.type)
		        	if (!params2.empty) {
		        		params2 += ", "
		        	}
		        	params2 += p.simpleName
	        	}
		        val params3 = params2
				if (retType.void) {
			        body = [
'''
return new SyncBladeRequest<Void>() {
    @Override
    public Void processSyncRequest() throws Exception {
        «name»(«params3»);
        return null;
    }
};
'''
			        ]
				} else {
			        body = [
'''
return new SyncBladeRequest<«retTypeObj»>() {
    @Override
    public «retTypeObj» processSyncRequest() throws Exception {
        return «name»(«params3»);
    }
};
'''
			        ]
				}
		        docComment = "SyncRequest for "+fqName+"("+params3+")"
    		])
    		if (reactor == null) {
    			reactor = context.newTypeReference(Reactor)
    		}
    		type.addMethod(name, [
		        visibility = Visibility.PUBLIC
		        final = true
		        static = false
		        returnType = retType
		        var params2 = ""
		        for (p : params) {
		        	addParameter(p.simpleName, p.type)
		        	if (!params2.empty) {
		        		params2 += ", "
		        	}
		        	params2 += p.simpleName
	        	}
		        val params3 = params2
	        	addParameter("sourceReactor", reactor)
				if (retType.void) {
			        body = [
'''
directCheck(sourceReactor);
«name»(«params3»);
'''
			        ]
				} else {
			        body = [
'''
directCheck(sourceReactor);
return «name»(«params3»);
'''
			        ]
				}
		        docComment = "direct-call for "+fqName+"("+params3+")"
    		])
    	} else {
    		context.addWarning(m, "Type of method annotated with @SReq ("+fqName+") must be a class")
    	}
    }
  }
}


/**
 * Generate the method returning a AsyncRequest for an instance method.
 *
 * Example:
 *
 * <code>
    // User written
    @AReq
    private void ping(AsyncRequest<Void> ar) {
        // NOP
    }

    // Generated!
    public AsyncRequest<Void> hangAReq() {
        return new AsyncBladeRequest<Void>() {
            @Override
            public void processAsyncRequest() throws Exception {
                hang(this);
            }
        };
    }
    </code>
 *
 * @author monster
 */
class AReqProcessor
      implements TransformationParticipant<MutableMethodDeclaration> {

  var TypeReference asyncRequest

  private def String checkFirstParam(TransformationContext context, String fqName,
  	Iterable<? extends MutableParameterDeclaration> params) {
  	if (params.empty) {
  		return "Method "+fqName+"(), annotated with @AReq, must receive AsyncRequest<*> as first parameter"
  	}
  	val type = params.get(0).type
  	if (asyncRequest == null) {
  		asyncRequest = context.newTypeReference(AsyncRequest)
  	}
  	if (!asyncRequest.isAssignableFrom(type)) {
  		return "Method "+fqName+"(..), annotated with @AReq, must receive AsyncRequest<*> as first parameter"
  	}
  	if (type.actualTypeArguments.empty) {
  		return "Method "+fqName+"(..), annotated with @AReq, must receive a *typed* AsyncRequest<*> as first parameter"
  	}
  	null
  }

  override doTransform(List<? extends MutableMethodDeclaration> methods,
                       extension TransformationContext context) {
    for (m : methods) {
    	val type = m.declaringType
    	val name = m.simpleName
    	val fqName = type.qualifiedName+"."+name
    	if (type instanceof MutableClassDeclaration) {
	    	val params = m.parameters
	    	val error = checkFirstParam(context, fqName, params)
	    	if (error != null) {
    			context.addWarning(m, error)
	    	} else {
		    	val genName = name+"AReq"
		    	val p0Type = params.get(0).type
	    		type.addMethod(genName, [
			        visibility = Visibility.PUBLIC
			        final = true
			        static = false
			        returnType = p0Type
			        var params2 = ""
			        for (p : params) {
			        	if (!params2.empty) {
				        	addParameter(p.simpleName, p.type)
			        		params2 += ", " + p.simpleName
			        	} else {
			        		params2 = "this"
			        	}
		        	}
			        val params3 = params2
			        body = [
'''
return new AsyncBladeRequest<«p0Type.actualTypeArguments.get(0)»>() {
    @Override
    public void processAsyncRequest() throws Exception {
        «name»(«params3»);
    }
};
'''
			        ]
			        docComment = "AsyncRequest for "+fqName+"("+params3+")"
	    		])
    		}
    	} else {
    		context.addWarning(m, "Type of method annotated with @AReq ("+fqName+") must be a class")
    	}
    }
  }
}