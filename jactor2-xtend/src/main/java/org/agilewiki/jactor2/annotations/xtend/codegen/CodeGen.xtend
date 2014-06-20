package org.agilewiki.jactor2.annotations.xtend.codegen

import java.util.List
import org.agilewiki.jactor2.core.reactors.Reactor
import org.agilewiki.jactor2.core.requests.AsyncRequest
import org.agilewiki.jactor2.core.requests.SyncRequest
import org.eclipse.xtend.lib.macro.TransformationContext
import org.eclipse.xtend.lib.macro.TransformationParticipant
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration
import org.eclipse.xtend.lib.macro.declaration.TypeReference
import org.eclipse.xtend.lib.macro.declaration.Visibility

/**
 * Generates the method returning a SyncRequest for an instance method, as
 * well as a "direct call" public method.
 * @author monster
 */
class SReqProcessor
      implements TransformationParticipant<MutableMethodDeclaration> {

	var TypeReference reactor

  private def String checkMethod(TransformationContext context, String name, String fqName) {
  	if (!name.startsWith("_")) {
  		return "Name of method "+fqName+"(), annotated with @SReq, must start with '_'"
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
	    	val error = checkMethod(context, name, fqName)
	    	if (error != null) {
    			context.addError(m, error)
	    	} else {
		    	val params = m.parameters
		    	val retType = m.returnType
		    	val genName = name.substring(1)+"SReq"
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
	    		type.addMethod(name.substring(1), [
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
		        	exceptions = m.exceptions
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
    		}
    	} else {
    		context.addError(m, "Type of method annotated with @SReq ("+fqName+") must be a class")
    	}
    }
  }
}


/**
 * Generates the method returning a AsyncRequest for an instance method, as
 * well as a "direct call" public method.
 * @author monster
 */
class AReqProcessor
      implements TransformationParticipant<MutableMethodDeclaration> {

	var TypeReference reactor

    var TypeReference asyncRequest

  private def String checkMethod(TransformationContext context, String name, String fqName,
  	Iterable<? extends MutableParameterDeclaration> params) {
  	if (!name.startsWith("_")) {
  		return "Name of method "+fqName+"(), annotated with @AReq, must start with '_'"
  	}
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
	    	val error = checkMethod(context, name, fqName, params)
	    	if (error != null) {
    			context.addError(m, error)
	    	} else {
		    	val genName = name.substring(1)+"AReq"
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
	    		if (reactor == null) {
	    			reactor = context.newTypeReference(Reactor)
	    		}
	    		type.addMethod(name.substring(1), [
			        visibility = Visibility.PUBLIC
			        final = true
			        static = false
			        returnType = context.primitiveVoid
			        var params2 = ""
			        for (p : params) {
			        	addParameter(p.simpleName, p.type)
			        	if (!params2.empty) {
			        		params2 += ", "
			        	}
			        	params2 += p.simpleName
		        	}
			        val params3 = params2
		        	exceptions = m.exceptions
				    body = [
	'''
	directCheck(ar.getTargetReactor());
	«name»(«params3»);
	'''
				    ]
			        docComment = "direct-call for "+fqName+"("+params3+")"
	    		])
    		}
    	} else {
    		context.addError(m, "Type of method annotated with @AReq ("+fqName+") must be a class")
    	}
    }
  }
}