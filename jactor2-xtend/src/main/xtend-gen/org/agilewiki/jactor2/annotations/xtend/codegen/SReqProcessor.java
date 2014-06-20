package org.agilewiki.jactor2.annotations.xtend.codegen;

import com.google.common.base.Objects;
import java.util.List;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.SyncRequest;
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
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

/**
 * Generate the method returning a SyncRequest for an instance method.
 * 
 * Example:
 * 
 * <code>
 * // User written
 * @SReq
 * private long _ping() {
 * count += 1;
 * return count;
 * }
 * 
 * // Generated!
 * public SyncRequest<Long> pingSReq() {
 * return new SyncBladeRequest<Long>() {
 * @Override
 * public Long processSyncRequest() {
 * return _ping();
 * }
 * };
 * }
 * public long ping(final Reactor sourceReactor) {
 * directCheck(sourceReactor);
 * return _ping();
 * }
 * </code>
 * 
 * @author monster
 */
@SuppressWarnings("all")
public class SReqProcessor implements TransformationParticipant<MutableMethodDeclaration> {
  private TypeReference reactor;
  
  private String checkMethod(final TransformationContext context, final String name, final String fqName) {
    String _xblockexpression = null;
    {
      boolean _startsWith = name.startsWith("_");
      boolean _not = (!_startsWith);
      if (_not) {
        return (("Name of method " + fqName) + "(), annotated with @SReq, must start with \'_\'");
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
          final String error = this.checkMethod(context, name, fqName);
          boolean _notEquals = (!Objects.equal(error, null));
          if (_notEquals) {
            context.addError(m, error);
          } else {
            final Iterable<? extends MutableParameterDeclaration> params = m.getParameters();
            final TypeReference retType = m.getReturnType();
            String _substring = name.substring(1);
            final String genName = (_substring + "SReq");
            final TypeReference retTypeObj = retType.getWrapperIfPrimitive();
            final Procedure1<MutableMethodDeclaration> _function = new Procedure1<MutableMethodDeclaration>() {
              public void apply(final MutableMethodDeclaration it) {
                it.setVisibility(Visibility.PUBLIC);
                it.setFinal(true);
                it.setStatic(false);
                TypeReference _newTypeReference = context.newTypeReference(SyncRequest.class, retTypeObj);
                it.setReturnType(_newTypeReference);
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
                boolean _isVoid = retType.isVoid();
                if (_isVoid) {
                  final CompilationStrategy _function = new CompilationStrategy() {
                    public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return new SyncBladeRequest<Void>() {");
                      _builder.newLine();
                      _builder.append("    ");
                      _builder.append("@Override");
                      _builder.newLine();
                      _builder.append("    ");
                      _builder.append("public Void processSyncRequest() throws Exception {");
                      _builder.newLine();
                      _builder.append("        ");
                      _builder.append(name, "        ");
                      _builder.append("(");
                      _builder.append(params3, "        ");
                      _builder.append(");");
                      _builder.newLineIfNotEmpty();
                      _builder.append("        ");
                      _builder.append("return null;");
                      _builder.newLine();
                      _builder.append("    ");
                      _builder.append("}");
                      _builder.newLine();
                      _builder.append("};");
                      _builder.newLine();
                      return _builder;
                    }
                  };
                  it.setBody(_function);
                } else {
                  final CompilationStrategy _function_1 = new CompilationStrategy() {
                    public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("return new SyncBladeRequest<");
                      _builder.append(retTypeObj, "");
                      _builder.append(">() {");
                      _builder.newLineIfNotEmpty();
                      _builder.append("    ");
                      _builder.append("@Override");
                      _builder.newLine();
                      _builder.append("    ");
                      _builder.append("public ");
                      _builder.append(retTypeObj, "    ");
                      _builder.append(" processSyncRequest() throws Exception {");
                      _builder.newLineIfNotEmpty();
                      _builder.append("        ");
                      _builder.append("return ");
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
                  it.setBody(_function_1);
                }
                it.setDocComment((((("SyncRequest for " + fqName) + "(") + params3) + ")"));
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
                it.setReturnType(retType);
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
                it.addParameter("sourceReactor", SReqProcessor.this.reactor);
                Iterable<? extends TypeReference> _exceptions = m.getExceptions();
                it.setExceptions(((TypeReference[])Conversions.unwrapArray(_exceptions, TypeReference.class)));
                boolean _isVoid = retType.isVoid();
                if (_isVoid) {
                  final CompilationStrategy _function = new CompilationStrategy() {
                    public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("directCheck(sourceReactor);");
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
                } else {
                  final CompilationStrategy _function_1 = new CompilationStrategy() {
                    public CharSequence compile(final CompilationStrategy.CompilationContext it) {
                      StringConcatenation _builder = new StringConcatenation();
                      _builder.append("directCheck(sourceReactor);");
                      _builder.newLine();
                      _builder.append("return ");
                      _builder.append(name, "");
                      _builder.append("(");
                      _builder.append(params3, "");
                      _builder.append(");");
                      _builder.newLineIfNotEmpty();
                      return _builder;
                    }
                  };
                  it.setBody(_function_1);
                }
                it.setDocComment((((("direct-call for " + fqName) + "(") + params3) + ")"));
              }
            };
            ((MutableClassDeclaration)type).addMethod(_substring_1, _function_1);
          }
        } else {
          context.addError(m, (("Type of method annotated with @SReq (" + fqName) + ") must be a class"));
        }
      }
    }
  }
}
