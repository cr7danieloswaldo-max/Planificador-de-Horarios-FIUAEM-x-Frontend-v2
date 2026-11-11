<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_cabecera.jspf" %>
<div class="card warn" style="max-width:600px">
  <h2>No hay horarios viables con tu selección</h2>
  <p><c:out value="${pantalla.motivo}"/></p>
  <p><b>Prueba esto:</b></p>
  <ul>
    <c:forEach var="s" items="${pantalla.sugerencias}"><li><c:out value="${s}"/></li></c:forEach>
  </ul>
  <a class="btn" href="planificador?paso=2">Ajustar selección</a>
</div>
<%@ include file="_pie.jspf" %>
