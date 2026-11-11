<%@ tag description="Turno y día libre (PanelPreferencias)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="panel" required="true" type="mx.uaemex.planificador.vista.PanelPreferencias" %>
<p class="mu" style="margin:0">Turno</p>
<c:forEach var="t" items="${panel.turnos}">
  <label class="row"><input type="radio" name="turno" value="${t.codigo}" ${panel.turnoSeleccionado(t) ? 'checked' : ''}> <c:out value="${t.etiqueta}"/></label>
</c:forEach>
<p class="mu" style="margin:8px 0 0">Día libre</p>
<c:forEach var="d" items="${panel.diasLibres}">
  <label class="row"><input type="checkbox" name="diaLibre" value="${d.codigo}" ${panel.diaLibreSeleccionado(d) ? 'checked' : ''}> <c:out value="${d.nombre}"/></label>
</c:forEach>
