<university name="${university.name}">
    <students count="${university.students?size}"><#list university.students as student>
        <student>${student.name}</student></#list>
        <student>${response.name}</student>
    </students>
</university>
