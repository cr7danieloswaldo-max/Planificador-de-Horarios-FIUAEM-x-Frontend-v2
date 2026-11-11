<%@ tag description="Lista de casillas de materias (ChecklistMaterias)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="checklist" required="true" type="mx.uaemex.planificador.vista.ChecklistMaterias" %>
<c:forEach var="m" items="${checklist.materias}">
  <c:choose>
    <c:when test="${not m.habilitada}">
      <div class="row dis">
        <input type="checkbox" disabled>
        <div><c:out value="${m.nombre}"/>
          <small><c:out value="${m.motivoBloqueo}"/></small></div>
      </div>
    </c:when>
    <c:otherwise>
      <label class="row">
        <input type="checkbox" name="${checklist.campo}" value="${fn:escapeXml(m.clave)}" ${checklist.estaMarcada(m.clave) ? 'checked' : ''}>
        <span style="flex:1"><c:out value="${m.nombre}"/></span>
      </label>
    </c:otherwise>
  </c:choose>
</c:forEach>
