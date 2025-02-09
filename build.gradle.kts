plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")

}

tasks.test {
    useJUnitPlatform()
}