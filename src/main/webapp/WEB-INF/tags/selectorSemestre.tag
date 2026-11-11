<%@ tag description="Radios de semestre (SelectorSemestre)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="selector" required="true" type="mx.uaemex.planificador.vista.SelectorSemestre" %>
<c:forEach var="o" items="${selector.opciones}">
  <label class="opt">
    <input type="radio" name="semestre" value="${o.valor}" ${o.seleccionada ? 'checked' : ''}
           onchange="this.form.accion.value='semestre'; this.form.submit()">
    <c:out value="${o.etiqueta}"/>
  </label>
</c:forEach>
