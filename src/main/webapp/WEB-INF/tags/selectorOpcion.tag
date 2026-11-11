<%@ tag description="Pestañas de opción (SelectorOpcion)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="selector" required="true" type="mx.uaemex.planificador.vista.SelectorOpcion" %>
<%@ attribute name="resumen" required="false" type="java.lang.String" %>
<div class="tabs">
  <c:forEach var="p" items="${selector.pestanas}">
    <a class="tab ${p.activa ? 'on' : ''}" href="planificador?paso=3&amp;opcion=${p.indice}"><c:out value="${p.etiqueta}"/></a>
  </c:forEach>
  <span class="mu"><c:out value="${resumen}"/></span>
</div>
