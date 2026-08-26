# Histórico de migraciones antiguo (retirado)

Estas migraciones **ya no se ejecutan** y se conservan solo como referencia histórica. Se sacaron de
`src/main/resources` para que no acabaran en el classpath de las aplicaciones.

Dos motivos:

1. **Están desincronizadas del modelo**: describen tablas `t_*` con claves `BIGINT`, mientras que las
   entidades actuales usan nombres en snake_case y claves `UUID`.
2. **El SQL está corrompido**: un reemplazo de `table` por `Game` mal hecho dejó sentencias como
   `create Game t_lang` o `alter Game t_user`, que no ejecutan.

El esquema vigente es el baseline `V3.0.0_1__Baseline.sql` de **CAH-Telegram**, generado desde las
entidades con `SchemaGenerator`. Los textos i18n de `V2.0.0_2__Tags.sql` y `V2.0.0_4__EnglishTags.sql`
sí se recuperaron, y viven ahora en `V3.0.0_2__Languages_and_tags.sql`.
