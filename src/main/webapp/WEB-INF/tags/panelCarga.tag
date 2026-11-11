<%@ tag description="Lista 'Tu carga' (PanelCarga)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="panel" required="true" type="mx.uaemex.planificador.vista.PanelCarga" %>
<div class="lista-carga">
  <c:forEach var="i" items="${panel.items}">
    <div class="item-carga">
      <div class="materia-titulo">
        <span class="dot ${i.color}"></span>
        <c:out value="${i.materia}"/>
      </div>
      <div class="mu materia-detalle">
        Grupo <c:out value="${i.grupo}"/> · <c:out value="${i.docente}"/>
      </div>
    </div>
  </c:forEach>
</div>
