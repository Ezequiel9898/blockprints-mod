package com.hollingsworth.schematic.siteexport.libs.mdast;


import com.google.gson.stream.JsonWriter;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstAnyContent;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstNode;

import java.io.IOException;

public class MdAstYamlFrontmatter extends MdAstNode implements MdAstAnyContent {
    public String value = "";

    public MdAstYamlFrontmatter() {
        super("yamlFrontmatter");
    }

    @Override
    protected void writeJson(JsonWriter writer) throws IOException {
        writer.name("value").value(value);
    }

    @Override
    public void toText(StringBuilder buffer) {
    }
}
