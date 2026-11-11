<%@ tag description="Calendario semanal (CalendarioSemanal)" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="calendario" required="true" type="mx.uaemex.planificador.vista.CalendarioSemanal" %>
<div class="cal">
  <table>
    <tr>
      <th></th>
      <c:forEach var="d" items="${calendario.dias}"><th><c:out value="${d.etiqueta}"/></th></c:forEach>
    </tr>
    <c:forEach var="h" items="${calendario.horas}">
      <tr>
        <td class="h">${h}:00</td>
        <c:forEach var="d" items="${calendario.dias}">
          <td>
            <c:forEach var="b" items="${calendario.bloquesEn(d, h)}">
              <div class="blk ${b.bloque.color}" style="top:${b.top}px;height:${b.alto}px">
                <c:out value="${b.bloque.etiqueta}"/>
                <small><c:out value="${b.bloque.docente}"/></small>
              </div>
            </c:forEach>
          </td>
        </c:forEach>
      </tr>
    </c:forEach>
  </table>
</div>
