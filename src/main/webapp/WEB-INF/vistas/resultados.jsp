<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_cabecera.jspf" %>
<div class="grid res">
  <div class="card">
    <ui:selectorOpcion selector="${pantalla.selectorOpcion}" resumen="${pantalla.resumen}"/>
    <c:if test="${not empty pantalla.avisoOpcionUnica}">
      <p class="mu"><c:out value="${pantalla.avisoOpcionUnica}"/></p>
    </c:if>
    <ui:calendario calendario="${pantalla.calendario}"/>
  </div>
  <div class="card">
    <h2>Tu carga</h2>
    <ui:panelCarga panel="${pantalla.panelCarga}"/>
  </div>
</div>
<div class="bar">
  <a class="btn o" href="planificador?paso=2">Ajustar preferencias</a>
  <button class="btn" type="button" onclick="window.print()">Imprimir o guardar como PDF</button>
</div>
<c:if test="${autoImprimir}">
  <script>window.addEventListener('load', function () { window.print(); });</script>
</c:if>
<%@ include file="_pie.jspf" %>
