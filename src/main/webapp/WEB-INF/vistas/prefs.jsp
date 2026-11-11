<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_cabecera.jspf" %>
<form method="post" action="planificador">
  <input type="hidden" name="accion" value="generar">
  <div class="grid">
    <div class="card">
      <h2>Materias que quieres cursar</h2>
      <ui:checklist checklist="${pantalla.listaDeseadas}"/>
      <p><b><span id="cuenta">${pantalla.contador}</span> materias seleccionadas</b></p>
    </div>
    <div class="card">
      <h2>Preferencias de horario</h2>
      <ui:panelPreferencias panel="${pantalla.panelPreferencias}"/>
    </div>
  </div>
  <div class="bar">
    <a class="btn o" href="planificador?paso=1">Atrás</a>
    <button class="btn" type="submit">Generar horarios</button>
  </div>
</form>
<script>
  // Contador en vivo (PantallaMateriasPreferencias.actualizarContador pone el valor inicial)
  const cuenta = document.getElementById('cuenta');
  document.querySelectorAll('input[name="sel"]').forEach(c => c.addEventListener('change',
    () => cuenta.textContent = document.querySelectorAll('input[name="sel"]:checked').length));
</script>
<%@ include file="_pie.jspf" %>
