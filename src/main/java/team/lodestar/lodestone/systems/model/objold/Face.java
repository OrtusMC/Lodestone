package team.lodestar.lodestone.systems.model.objold;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public record Face(List<Vertex> vertices) {
    public void renderFace(PoseStack poseStack, RenderType renderType, int packedLight) {
        MultiBufferSource.BufferSource mcBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        //MultiBufferSource.BufferSource lodestoneBufferSource = RenderHandler.DELAYED_RENDER.getTarget();
        VertexConsumer buffer = mcBufferSource.getBuffer(renderType);
        int vertexCount = vertices.size();

        if (vertexCount == 4) renderQuad(poseStack, buffer, packedLight);
        else if (vertexCount == 3) renderTriangle(poseStack, buffer, packedLight);
        else throw new RuntimeException("Face has invalid number of vertices. Supported vertex counts are 3 and 4.");
    }
    public void renderTriangle(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices().forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
        addVertex(buffer, this.vertices().get(0), poseStack, packedLight);
    }

    public void renderQuad(PoseStack poseStack, VertexConsumer buffer, int packedLight) {
        this.vertices().forEach(vertex -> addVertex(buffer, vertex, poseStack, packedLight));
    }

    private void addVertex(VertexConsumer buffer, Vertex vertex, PoseStack poseStack, int packedLight) {
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        Vector3f position = vertex.position();
        Vector3f normal = vertex.normal();
        Vec2 uv = vertex.uv();

        buffer.addVertex(matrix4f, position.x(), position.y(), position.z())
                .setColor(255, 255, 255, 255)
                .setUv(uv.x, -uv.y)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(poseStack.last(), normal.x(), normal.y(), normal.z())
                .setLight(packedLight);
    }

    public Vector3f getCentroid() {
        Vector3f centroid = new Vector3f();
        for (Vertex vertex : vertices) centroid.add(vertex.position());
        centroid.div(vertices.size());
        return centroid;
    }
}