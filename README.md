# Micrometrics

A small Spring MVC project to experiment with Micrometer, Prometheus and Grafana.

## Useful curl commands

* Prometheus endpoint: 
`curl -s http://localhost:8080/actuator/prometheus|grep device_usage|grep active`
* List device States: `watch "curl -s http://localhost:8080/device|jq"`
* Update the state of a device: `curl -XPUT http://localhost:8080/device/1/state/OFFLINE`