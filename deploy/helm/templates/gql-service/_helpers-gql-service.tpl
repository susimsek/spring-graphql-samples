{{- define "spring-gql-service.selectorLabels" -}}
app: spring-gql-service
release: {{ .Release.Name }}
{{- end }}

{{- define "spring-gql-service.labels" -}}
chart: {{ include "app.chart" . }}
{{ include "spring-gql-service.selectorLabels" . }}
heritage: {{ .Release.Service }}
app: spring-gql-service
{{- end }}