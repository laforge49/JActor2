package org.agilewiki.jactor2.annotations.xtend.codegen;

import com.google.common.base.Objects;
import java.util.List;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.AsyncRequest;
import org.eclipse.xtend.lib.macro.TransformationContext;
import org.eclipse.xtend.lib.macro.TransformationParticipant;
import org.eclipse.xtend.lib.macro.declaration.CompilationStrategy;
import org.eclipse.xtend.lib.macro.declaration.MutableClassDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableMethodDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableParameterDeclaration;
import org.eclipse.xtend.lib.macro.declaration.MutableTypeDeclaration;
import org.eclipse.xtend.lib.macro.declaration.TypeReference;
import org.eclipse.xtend.lib.macro.declaration.Visibility;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Generate the method returning a AsyncRequest for an instance method.
 * 
 * Example:
 * 
 * <code>
 * // User written
 * @AReq
 * private void _hang(AsyncRequest<Void> ar) {
 * // NOP
 * }
 * 
 * // Generated!
 * public AsyncRequest<Void> hangAReq() {
 * return new AsyncBladeRequest<Void>() {
 * @Override
 * public void processAsyncRequest() throws Exception {
 * _hang(this);
 * }
 * };
 * }
 * public long hang(final AsyncRequest<Void> ar) {
 * // Might get NPE here ...
 * directCheck(ar.getTargetReactor());
 * return _hang(ar);
 * }
 * </code>
 * 
 * @author monster
 */
@SuppressWarnings("all")
public class AReqProcessor implements TransformationParticipant<MutableMethodDeclaration> {
  private TypeReference reactor;
  
  private TypeReference asyncRequest;
  
  private String checkMethod(final TransformationContext context, final String name, final String fqName, final Iterable<? extends MutableParameterDeclaration> params) {
    String _xblockexpression = null;
    {
      boolean _startsWith = name.startsWith("_");
      boolean _not = (!_startsWith);
      if (_not) {
        return (("Name of method " + fqName) + "(), annotated with @AReq, must start with \'_\'");
      }
      boolean _isEmpty = IterableExtensions.isEmpty(params);
      if (_isEmpty) {
        return (("Method " + fqName) + "(), annotated with @AReq, must receive AsyncRequest<*> as first parameter");
      }
      MutableParameterDeclaration _get = ((MutableParameterDeclaration[])Conversions.unwrapArray(params, MutableParameterDeclaration.class))[0];
      final TypeReference type = _get.getType();
      boolean _equals = Objects.equal(this.asyncRequest, null);
      if (_equals) {
        TypeReference _newTypeReference = context.newTypeReference(AsyncRequest.class);
        this.asyncRequest = _newTypeReference;
      }
      boolean _isAssignableFrom = this.asyncRequest.isAssignableFrom(type);
      boolean _not_1 = (!_isAssignableFrom);
      if (_not_1) {
        return (("Method " + fqName) + "(..), annotated with @AReq, must receive AsyncRequest<*> as first parameter");
      }
      List<TypeReference> _actualTypeArguments = type.getActualTypeArguments();
      boolean _isEmpty_1 = _actualTypeArguments.isEmpty();
      if (_isEmpty_1) {
        return (("Method " + fqName) + "(..), annotated with @AReq, must receive a *typed* AsyncRequest<*> as first parameter");
      }
      _xblockexpression = null;
    }
    return _xblockexpression;
  }
  
  public void doTransform(final List<? extends MutableMethodDeclaration> methods, @Extension final TransformationContext context) {
    for (final MutableMethodDeclaration m : methods) {
      {
        final MutableTypeDeclaration type = m.getDeclaringType();
        final String name = m.getSimpleName();
        String _qualifiedName = type.getQualifiedName();
        String _plus = (_qualifiedName + ".");
        final String fqName = (_plus + name);
        if ((type instanceof MutableClassDeclaration)) {
          final Iterable<? extends MutableParameterDeclaration> params = m.getParameters();
          final String error = this.checkMethod(context, name, fqName, params);
          boolean _notEquals = (!Objects.equal(error, null));
          if (_notEquals) {
            context.addError(m, error);
          } else {
            String _substring = name.substring(1);
            final String genName = (_substring + "AReq");
            MutableParameterDeclaration _get = ((MutableParameterDeclaration[])Conversions.unwrapArray(params, MutableParameterDeclaration.class))[0];
            final TypeReference p0Type = _get.getType();
            final Procedure1<MutableMethodDeclaration> _function = new Procedure1<MutableMethodDeclaration>() {
              public void apply(final MutableMethodDeclaration it) {
                it.setVisibility(Visibility.PUBLIC);
                it.setFinal(true);
                it.setStatic(false);
                it.setReturnType(p0Type);
                String params2 = "";
                for (final MutableParameterDeclaration p : params) {
                  boolean _isEmpty = params2.isEmpty();
                  boolean _not = (!_isEmpty);
                  if (_not) {
                    String _simpleName = p.getSimpleName();
                    TypeReference _type = p.getType();
                    it.addParameter(_simpleName, _type);
                    String _params2 = params2;
                    String _simpleName_1 = p.getSimpleName();
                    String _plus = (", " + _simpleName_1);
                    params2 = (_params2 + _plus);
                  } else {
                    params2 = "this";
                  }
                }
                final String params3 = params2;
                final CompilationStrategy _function = new CompilationStrategy() {
                  public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("return new AsyncBladeRequest<");
                    List<TypeReference> _actualTypeArguments = p0Type.getActualTypeArguments();
                    TypeReference _get = _actualTypeArguments.get(0);
                    _builder.append(_get, "");
                    _builder.append(">() {");
                    _builder.newLineIfNotEmpty();
                    _builder.append("    ");
                    _builder.append("@Override");
                    _builder.newLine();
                    _builder.append("    ");
                    _builder.append("public void processAsyncRequest() throws Exception {");
                    _builder.newLine();
                    _builder.append("        ");
                    _builder.append(name, "        ");
                    _builder.append("(");
                    _builder.append(params3, "        ");
                    _builder.append(");");
                    _builder.newLineIfNotEmpty();
                    _builder.append("    ");
                    _builder.append("}");
                    _builder.newLine();
                    _builder.append("};");
                    _builder.newLine();
                    return _builder;
                  }
                };
                it.setBody(_function);
                it.setDocComment((((("AsyncRequest for " + fqName) + "(") + params3) + ")"));
              }
            };
            ((MutableClassDeclaration)type).addMethod(genName, _function);
            boolean _equals = Objects.equal(this.reactor, null);
            if (_equals) {
              TypeReference _newTypeReference = context.newTypeReference(Reactor.class);
              this.reactor = _newTypeReference;
            }
            String _substring_1 = name.substring(1);
            final Procedure1<MutableMethodDeclaration> _function_1 = new Procedure1<MutableMethodDeclaration>() {
              public void apply(final MutableMethodDeclaration it) {
                it.setVisibility(Visibility.PUBLIC);
                it.setFinal(true);
                it.setStatic(false);
                TypeReference _primitiveVoid = context.getPrimitiveVoid();
                it.setReturnType(_primitiveVoid);
                String params2 = "";
                for (final MutableParameterDeclaration p : params) {
                  {
                    String _simpleName = p.getSimpleName();
                    TypeReference _type = p.getType();
                    it.addParameter(_simpleName, _type);
                    boolean _isEmpty = params2.isEmpty();
                    boolean _not = (!_isEmpty);
                    if (_not) {
                      String _params2 = params2;
                      params2 = (_params2 + ", ");
                    }
                    String _params2_1 = params2;
                    String _simpleName_1 = p.getSimpleName();
                    params2 = (_params2_1 + _simpleName_1);
                  }
                }
                final String params3 = params2;
                Iterable<? extends TypeReference> _exceptions = m.getExceptions();
                it.setExceptions(((TypeReference[])Conversions.unwrapArray(_exceptions, TypeReference.class)));
                final CompilationStrategy _function = new CompilationStrategy() {
                  public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                    StringConcatenation _builder = new StringConcatenation();
                    _builder.append("directCheck(ar.getTargetReactor());");
                    _builder.newLine();
                    _builder.append(name, "");
                    _builder.append("(");
                    _builder.append(params3, "");
                    _builder.append(");");
                    _builder.newLineIfNotEmpty();
                    return _builder;
                  }
                };
                it.setBody(_function);
                it.setDocComment((((("direct-call for " + fqName) + "(") + params3) + ")"));
              }
            };
            ((MutableClassDeclaration)type).addMethod(_substring_1, _function_1);
          }
        } else {
          context.addError(m, (("Type of method annotated with @AReq (" + fqName) + ") must be a class"));
        }
      }
    }
  }
}
