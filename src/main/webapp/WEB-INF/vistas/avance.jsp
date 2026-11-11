<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="_cabecera.jspf" %>
<form method="post" action="planificador">
  <input type="hidden" name="accion" value="avance">
  <div class="grid">
    <div class="card">
      <h2>¿A qué semestre te reinscribes?</h2>
      <ui:selectorSemestre selector="${pantalla.selectorSemestre}"/>
      <p class="mu">Con esto se muestran las materias que puedes elegir.</p>
    </div>
    <div class="card">
      <h2>Materias que ya aprobaste</h2>
      <ui:checklist checklist="${pantalla.listaAprobadas}"/>
      <p class="mu">Tú declaras lo que cursaste; el sistema no lo valida contra Control Escolar.</p>
    </div>
  </div>
  <div class="bar">
    <a class="btn o" href="planificador?paso=0">Atrás</a>
    <button class="btn" type="submit">Siguiente: materias y preferencias</button>
  </div>
</form>
<%@ include file="_pie.jspf" %>
